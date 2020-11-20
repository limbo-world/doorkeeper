
import ui from './ui';
import session from './session';

Vue.use(Vuex);

const GlobalStore = new Vuex.Store({
    // 初始状态声明，通过mapState可以得到快捷访问函数，解构声明到computed中可以使用
    state: {},

    // 访问getter，可以做特殊处理，mapGetters
    // 第一个参数是模块内的状态，第三个参数是全部的状态
    getters: {},

    // 声明一个变更事件mutation，变量名代表事件类型，变量值(函数)代表事件回调函数
    // 事件可以带一个额外的参数，表示负载参数
    // 提交mutation有两种方式：  $store.commit('toggleMenu', payload)    $store.commit({type: 'toggleMenu', payload: {}'})
    // ！！！mutations必须是同步函数，不能在内部调用异步函数修改state中的状态
    // ！！！如果一定要异步修改，使用action
    // mapMutations
    mutations: {},

    // action不能直接修改state，必须通过提交事件来修改，同步或者异步都行；action可以带一个负载参数；
    // ！！！参数context并不是store
    // 触发action的方式为：  $store.dispatch('asyncToggleMenu');  $store.dispatch({type: 'asyncToggleMenu', payload: {}})
    // 一个action中可以提交多个mutation，而且可以异步提交；如果action的回调函数返回了一个Promise，则$store.dispatch将返回这个Promise
    // 一个action中可以提交另一个action
    // mapActions
    actions: {},

    // 分模块管理状态，为了不把全部的状态都声明在一个js中，可以在多个js中声明多种类型状态的处理getter、mutation、action，
    // 然后通过module把这些js组合到store中
    // 模块内的mutation、getter接收的state参数是模块内的state；
    // 模块内的action接收的context是模块的上下文，如果想要访问全部的state，用context.rootState
    modules: {
        ui, session
    }
});


export default GlobalStore;
