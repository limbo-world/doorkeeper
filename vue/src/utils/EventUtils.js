/**
 * 节流函数，封装一下回调，在需要频繁调用回调函数时，会保证两次调用之间至少间隔interval毫秒
 * @param fn            需要执行的回调
 * @param interval      节流间隔
 * @returns {Function}  封装后的函数
 */
const throttle = (fn, interval = 500) => {
    let timer = null;
    let firstTime = true;

    return function (...args) {
        if (firstTime) {
            // 第一次加载
            fn.apply(this, args);
            return firstTime = false;
        }
        if (timer) {
            // 定时器正在执行中，跳过
            return;
        }
        timer = setTimeout(() => {
            clearTimeout(timer);
            timer = null;
            fn.apply(this, args);
        }, interval);
    };
};


/**
 * 防抖函数，封装一下回调，在多次触发回调函数时，只有最后一次触发的delay毫秒后才执行回调
 * @param fn            回调函数
 * @param delay         防抖延迟，最后一次触发到实际执行的延迟
 * @returns {Function}  封装后的函数
 */
const debounce = (fn, delay = 500) => {
    let timer = null;
    return function (...args) {
        if (timer) {
            clearTimeout(timer);
        }

        timer = setTimeout(() => {
            timer = null;
            fn.apply(this, args);
        }, delay);
    }
};


export default {
    debounce,
    throttle,
}
