import EventUtils from '../../../utils/EventUtils';

// 全局静态配置
window.AnimateCss = {
    config: {
        /**
         * 何时触发动画
         * immediate 立刻触发，默认值
         * click     单机时触发，未实现
         * hover     鼠标hover时触发，未实现
         * scroll    滚动到可见时触发
         */
        when: 'immediate',

        /**
         * 动画类名，基于animate.css
         */
        className: '',

        /**
         * 延迟触发时间，在满足触发时机时，再次延迟触发，单位毫秒
         */
        delay: 0,

        /**
         * 重复触发次数，默认只触发一次；小于等于0表示无限次触发；
         * when=immediate时该配置忽略
         */
        repeatTimes: {
            immediate: -1,
            click: -1,
            hover: -1,
            scroll: 1,
        },

        /**
         * 动画完成后是否移除动画类
         */
        removeClass: false,

        /**
         * when=scroll时有效
         * 可滚动区域的父元素，可以是一个选择器，或者是一个dom
         * 如果不是body，则这个scrollContainer的position必须有值，relative也行，否则将无法计算相对于滚动区域的偏移量
         */
        scrollContainer: 'body',

        /**
         * when=scroll时有效
         * 元素距离滚动顶部多远时触发动画
         */
        scrollBottom: -50,

    }
};

const When = {
    immediate: 'immediate',
    click: 'click',
    hover: 'hover',
    scroll: 'scroll',
};

class Animate {

    constructor(el, option, vn) {
        this.el = el;
        this.option = option;
        this.vn = vn;
        this.$repeatTimes = 0;
        this.animating = false;

        if (this.option.when === When.immediate) {
            // 立即触发动画，只能触发一次，会忽略触发次数配置
            this.addAnimateClass();
        } else if (this.option.when === When.hover) {
            this._bindEventWhenUseHover();
        } else if (this.option.when === When.scroll) {
            this._bindEventWhenUseScroll();
        }
    }

    /**
     * 当when=hover时，绑定mouseenter mouseleave事件
     * @private
     */
    _bindEventWhenUseHover() {
        this.onMouseEnterWhenHover = () => {
            this.addAnimateClass();
        };
        this.onMouseLeaveWhenHover = () => {
            this.removeAnimateClass();
        };
        this.el.addEventListener('mouseenter', this.onMouseEnterWhenHover);
        this.el.addEventListener('mouseleave', this.onMouseLeaveWhenHover);
    }

    /**
     * 当when=scroll时，绑定scroll事件
     * @private
     */
    _bindEventWhenUseScroll() {
        // 可见时触发，先找到滚动父元素
        if (isDom(this.option.scrollContainer)) {
            this.$scrollContainer = this.option.scrollContainer;
        } else if (typeof this.option.scrollContainer === 'string') {
            this.$scrollContainer = document.querySelector(this.option.scrollContainer);
        } else {
            this.$scrollContainer = document.body;
        }

        // 给父元素添加scroll事件监听
        this.onScrollWhenVisible = this._createOnScrollWhenVisible();
        this.$scrollContainer.addEventListener('scroll', EventUtils.throttle(this.onScrollWhenVisible, 100));
        // 进入页面第一次就要触发一下，防止第一次就在可视区域，但因为不滚动不触发动画
        this.vn.context.$nextTick(() => {
            this.onScrollWhenVisible();
        });
    }

    _createOnScrollWhenVisible() {
        const that = this;
        return function () {
            const el = that.el;
            // 动画元素的顶部偏移量
            let offsetTop = that._calculateOffsetTop(el);
            offsetTop = Number.isNaN(offsetTop) ? 0 : offsetTop;
            // 当前滚动高度
            let scrollTop = that.$scrollContainer.scrollTop || window.pageYOffset
                || document.documentElement.scrollTop || document.body.scrollTop;
            // 滚动区域高度
            let scrollContainerHeight = that.$scrollContainer.clientHeight;

            if (scrollContainerHeight + scrollTop + that.option.scrollBottom >= offsetTop) {
                that.addAnimateClass();
            }
        }
    }

    _calculateOffsetTop(el) {
        let offsetTop = 0;
        while (el !== this.$scrollContainer && el != null) {
            let ot = el.offsetTop;
            ot = Number.isNaN(ot) ? 0 : ot;
            offsetTop += ot;
            el = el.offsetParent;
        }
        return offsetTop;
    }


    addAnimateClass() {
        // 正在动画执行中不触发
        if (this.animating) {
            return;
        }

        // 检测已经触发了几次
        if (this.option.repeatTimes > 0 && this.option.repeatTimes <= this.$repeatTimes) {
            // 超出触发次数，提前销毁
            this.destroy();
            return;
        }

        // 计算新的类名
        let classes = !this.el.className ? [] : this.el.className.split(/\s+/);
        classes = classes.concat(this.option.className.split(/\s+/));
        let newClass = classes.join(' ');
        this.animating = true;

        // 检测是否包含animated类名
        if (!newClass.includes('animated')) {
            newClass += ` animated`;
        }

        // 检测延迟
        if (this.option.delay > 0) {
            setTimeout(() => {
                this.el.className = newClass;
                // animate.css动画时间1s
                if (this.option.removeClass) {
                    setTimeout(() => this.removeAnimateClass(), 1000);
                }
                this.$repeatTimes++;
            }, this.option.delay);
        } else {
            this.el.className = newClass;
            // animate.css动画时间1s
            if (this.option.removeClass) {
                setTimeout(() => this.removeAnimateClass(), 1000);
            }
            this.$repeatTimes++;
        }
    }

    removeAnimateClass() {
        let classes = !this.el.className ? [] : this.el.className.split(/\s+/);
        let added = this.option.className.split(/\s+/);
        let newClasses = [];
        classes.forEach(cn => {
            if (added.findIndex(c => c === cn) < 0) {
                newClasses.push(cn);
            }
        });
        this.el.className = newClasses.join(' ');
        this.animating = false;
    }

    destroy() {
        if (this.option.when === When.hover) {
            if (this.onMouseEnterWhenHover) {
                this.el.removeEventListener('mouseenter', this.onMouseEnterWhenHover);
            }
            if (this.onMouseLeaveWhenHover) {
                this.el.removeEventListener('mouseleave', this.onMouseLeaveWhenHover);
            }
        } else if (this.option.when === When.scroll) {
            this.$scrollContainer.removeEventListener('scroll', EventUtils.throttle(this.onScrollWhenVisible, 100));
        }
    }

}


function isDom(element) {
    return element instanceof Element || element instanceof HTMLDocument;
}

function parseOptionFromBinding(binding) {
    // 拷贝配置
    const option = JSON.parse(JSON.stringify(window.AnimateCss.config));
    if (typeof binding.value === 'string') {
        option.className = binding.value;
    } else {
        Object.keys(option).forEach(k => {
            if (binding.value[k] != null) {
                option[k] = binding.value[k];
            }
        });
    }

    // 检测修饰符，多个修饰符只有一个生效，生效顺序
    // scroll hover click immediate
    Object.keys(When).forEach(k => {
        if (binding.modifiers[k]) {
            option.when = When[k];
        }
    });

    option.repeatTimes = option.repeatTimes[option.when];
    return option;
}

export default {

    When,

    bind(el, binding, vn) {
        // 拷贝配置
        const option = parseOptionFromBinding(binding);

        // 如果已经存在，返回并warn
        if (vn.$animates) {
            if (vn.$animates.findIndex(an => an.option.when === option.when) >= 0) {
                console.warn('已经存在一个animate绑定!', option);
                return;
            }
        } else {
            vn.$animates = [];
        }

        vn.$animates.push(new Animate(el, option, vn));
    },

    unbind(el, binding, vn) {
        const option = parseOptionFromBinding(binding);

        if (vn.$animates && vn.$animates.length > 0) {
            const idx = vn.$animates.findIndex(an => an.option.when === option.when);
            if (idx >= 0 && vn.$animates[idx]) {
                vn.$animates[idx].destroy();
            }
        }
    },


}
