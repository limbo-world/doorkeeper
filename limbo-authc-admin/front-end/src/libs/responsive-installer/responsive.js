import EventUtils from '../../utils/EventUtils';

export default class Responsive {

    constructor(Vue) {
        this.vue = Vue;

        this.responsiveDefines = [];
        this.responsiveDefines.push({
            name: 'MAX',
            width: Number.MAX_VALUE
        });

        const that = this;
        window.addEventListener('resize', EventUtils.debounce(e => {
            const win = e.srcElement;
            const width = win.innerWidth;
            that._parseResponsiveName(width);
        }), 100);

        this._initVM({
            name: 'MAX'
        });
        this._parseResponsiveName(window.innerWidth);
    }

    get name() {
        return this._vm.name;
    }

    set name(name) {
        this._vm.$set(this._vm, 'name', name);
    }

    _initVM(data) {
        // 初始化一个Vue对象（视图对象 view-model）；
        // 当视图对象vm的数据发生变化时，会触发set
        const silent = this.vue.config.silent;
        this.vue.config.silent = true;
        this._vm = new this.vue({data});
        this.vue.config.silent = silent
    }

    _parseResponsiveName(width) {
        let newName;
        for (let i = 0; i < this.responsiveDefines.length; i++) {
            const d = this.responsiveDefines[i];
            if (width <= d.width) {
                newName = d.name;
                break;
            }
        }

        if (newName && newName !== this.name) {
            const target = this._vm;
            target.$set(target, 'name', newName);
            target.$forceUpdate();
        }

    }

    // 设置
    setDefaultResponsiveName(name) {
        const def = this.responsiveDefines.find(d => d.width === Number.MAX_VALUE);
        if (!def) {
            this.responsiveDefines.push({
                name, width: Number.MAX_VALUE
            })
        } else {
            def.name = name;
        }
    };

    // 定义一个响应式状态名，并指定最小宽度
    defineResponsive(name, minWidth) {
        this.responsiveDefines.push({
            name,
            width: minWidth
        });
        this.responsiveDefines.sort((d1, d2) => d1.width - d2.width);
        this._parseResponsiveName(window.innerWidth);
    }

    // 现在应用的响应式状态是否与参数传入的相同
    is(name) {
        return this.name === name;
    }

}
