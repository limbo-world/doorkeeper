<template>
    <el-progress :class="progressVisible ? '' : 'opacity-hidden'" class="progress-bar"
                 :percentage="progress" :color="colors" :stroke-width="2">
    </el-progress>
</template>



<script>

    import { mapState } from 'vuex';

    export default {
        data() {
            return {
                colors: [
                    {color: '#f56c6c', percentage: 20},
                    {color: '#e6a23c', percentage: 40},
                    {color: '#5cb87a', percentage: 60},
                    {color: '#1989fa', percentage: 80},
                    {color: '#6f7ad3', percentage: 100}
                ]
            }
        },

        computed: {
            ...mapState('ui', ['progress', 'progressVisible']),
        },

        created() {
            pages.layoutProgressBar = this;
        }

    }
</script>


<style lang="scss">
    .progress-bar.el-progress {
        transition: opacity .5s;
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: 100;

        .el-progress-bar {
            display: block;
            padding-right: 0;
        }
        .el-progress__text {
            display: none;
        }
    }
</style>
