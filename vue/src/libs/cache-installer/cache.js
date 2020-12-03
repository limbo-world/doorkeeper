import TimeUnit from './time-unit';

const CacheHolderType = {
    Local: window.localStorage,
    Session: window.sessionStorage,
};

export default class Cache {

    constructor(options) {
        options = options ? options : {};
        // 生成默认缓存配置
        const random = (Math.random() * 10000000000).toFixed(0);
        const defaultOptions = {
            name: `$$cache_${random}_$$`,         // 缓存名，在storage中存储时，拼接在缓存key前面，并与key通过 : 分隔
            cacheHolder: CacheHolderType.Local,   // 缓存类型，localStorage 或 sessionStorage
            expireDuration: 2,                    // 过期时间，默认两小时过期时间
            expireUnit: TimeUnit.Hours,           // 过期时间单位
        };

        // 合并自定义配置
        Object.keys(defaultOptions).forEach(k => {
            if (options[k] != null) {
                defaultOptions[k] = options[k];
            }
        });
        this.options = defaultOptions;

        // 定义缓存key的默认提供者
        this.providers = {};
    }

    // 定义一个key的提供者，当通过get方法取不到value时，会自动调用该方法，得到一个值并设置到缓存中
    defineValueProvider(k, fun, expire, expireUnit) {
        expire = expire != null ? expire : this.options.expireDuration;
        expireUnit = expireUnit != null ? expireUnit : this.options.expireUnit;

        if (typeof fun !== 'function') {
            console.warn(`缓存${this.options.name} key:${k}的provider不是一个function`);
            return;
        }

        this.providers[k] = {
            supplier: fun,
            expire,
            expireUnit,
        };

        return this;
    }

    // 设置一个缓存
    set(k, v, expire, expireUnit) {
        v = v ? v : null;
        expire = expire != null ? expire : this.options.expireDuration;
        expireUnit = expireUnit != null ? expireUnit : this.options.expireUnit;

        let wrapped = this.__get(k);
        wrapped = wrapped == null ? {
            created: Date.now()
        } : wrapped;
        wrapped.updated = Date.now();
        wrapped.data = JSON.stringify(v);
        wrapped.expire = expire;
        wrapped.expireUnit = expireUnit.name;

        const holder = this.options.cacheHolder;
        holder.setItem(`${this.options.name}:${k}`, JSON.stringify(wrapped));
    }

    // 异步读取一个缓存，如果不存在或已经过期，会尝试从provider中生成
    // 返回的是一个Promise，
    get(k) {
        return new Promise((resolve, reject) => {
            const wrapped = this.__getUnexpired(k);
            if (wrapped != null) {
                resolve(JSON.parse(wrapped.data));
                return;
            }

            let provider = this.providers[k];
            if (provider != null) {// key provider存在，则使用provider
                const result = provider.supplier();
                if (!!result) {// 返回值不为空
                    if (typeof result.then === 'function') {
                        // 判断返回值有then方法，认为是一个Promise
                        result.then(v => {
                            this.set(k, v, provider.expire, provider.expireUnit);
                            resolve(v);
                        }).catch(reject);
                        return;
                    } else {
                        // 没有then方法，使用返回值作为
                        this.set(k, result, provider.expire, provider.expireUnit);
                        resolve(result);
                        return;
                    }
                }
            }

            reject('key 不存在');
        });
    }

    /**
     * 同步读取一个缓存，如果不存在或已经过期，则返回null
     * 同步读取时无法使用provider，因为不知道provider返回的是不是Promise
     */
    getSync(k) {
        const wrapped = this.__getUnexpired(k);
        if (wrapped != null) {
            return JSON.parse(wrapped.data);
        }

        return null;
    }

    __getUnexpired(k) {
        let wrapped = this.__get(k);
        if (!wrapped) {
            return null;
        }

        // 负数的过期时间为永不过期
        if (wrapped.expire < 0) {
            return wrapped;
        }

        const expireDurationMills = wrapped.expireUnit.toMillis(wrapped.expire);
        // 如果缓存过期了，返回null
        if (wrapped.updated + expireDurationMills < Date.now()) {
            return null;
        } else {
            return wrapped;
        }
    }

    __get(k) {
        const holder = this.options.cacheHolder;
        const __json = holder.getItem(`${this.options.name}:${k}`);
        if (!__json) {
            return null;
        }
        return JSON.parse(__json, (key, value) => {
            // 修改timeUnit
            if (key === 'expireUnit') {
                return TimeUnit[value];
            }
            return value;
        });
    }

    // 移除key
    remove(k) {
        const holder = this.options.cacheHolder;
        holder.removeItem(`${this.options.name}:${k}`);
    }

}

Cache.prototype.Type = CacheHolderType;
