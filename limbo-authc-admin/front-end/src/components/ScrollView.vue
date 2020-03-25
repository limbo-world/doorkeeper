<template>
    <div class="scroll-container" ref="scrollContainer" @wheel.prevent="handleScroll">
        <div class="scroll-visible-area" ref="visibleArea" :style="{top: scrollTop + 'px'}">
            <slot></slot>
        </div>
    </div>

</template>


<script>

    /**
     * 使用组件时必须指定高度
     */
    export default {
        props: {
            'debounceDelta': {
                type: Number,
                default: 10
            }
        },

        data() {
            return {
                scrollTop: 0
            };
        },

        methods: {

            handleScroll(e) {
                const eventDelta = e.wheelDelta || -e.deltaY * 3;
                const $container = this.$refs.scrollContainer;
                const $containerHeight = $container.offsetHeight;
                const $visibleArea = this.$refs.visibleArea;
                const $visibleHeight = $visibleArea.offsetHeight;

                if (eventDelta > 0) {
                    this.scrollTop = Math.min(0, this.scrollTop + eventDelta)
                } else {
                    if ($containerHeight - this.debounceDelta < $visibleHeight) {
                        if (this.scrollTop < -($visibleHeight - $containerHeight + this.debounceDelta)) {
                            this.scrollTop = this.scrollTop;
                        } else {
                            this.scrollTop = Math.max(this.scrollTop + eventDelta, $containerHeight - $visibleHeight - this.debounceDelta)
                        }
                    } else {
                        this.scrollTop = 0
                    }
                }
            }
        }
    }
</script>


<style scoped lang="scss">
    .scroll-container {
        width: 100%;
        position: relative;
        background-color: transparent;

        .scroll-visible-area {
            position: absolute;
            width: 100% !important;
        }
    }
</style>
