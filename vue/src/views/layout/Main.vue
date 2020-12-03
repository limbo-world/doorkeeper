<template>
    <section class="app-main">
        <transition name="fade" mode="out-in">
            <router-view></router-view>
        </transition>
    </section>
</template>

<script>
    export default {
        name: 'AppMain'
    }
</script>

<style scoped>
    .app-main {
        height: 100%;
    }
</style>


<style scoped>
.fade-enter-active,
.fade-leave-active {
    opacity: 1;
    transition: opacity .5s;
}

.fade-enter,
.fade-leave-active {
    opacity: 0;
}
</style>
