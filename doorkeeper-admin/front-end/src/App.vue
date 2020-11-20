<template>
    <div id="app">
        <router-view/>
        {{isMenuHidden}}
    </div>
</template>

<script>
    import '@/assets/styles/reset.scss';
    import '@/libs/colorui/colorui.css';

    export default {
        name: 'App',

        created() {
            window.pages = {};
        },
    }
</script>
