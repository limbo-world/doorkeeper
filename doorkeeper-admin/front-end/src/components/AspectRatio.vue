<template>
    <div class="outter" :style="formattedRatioStyle">
        <div class="inner">
            <slot></slot>
        </div>
    </div>
</template>

<script>
    export default {
      data() {
        return {};
      },

      props: ['ratio'],

      computed: {
        formattedRatioStyle() {
          let ratio = this.ratio;
          if (ratio == null || Number.isNaN(ratio)) {
            ratio = 100;
          }
          if (typeof ratio !== 'string' || !ratio.endsWith('%')) {
            ratio = ratio + '%';
          }
          return `padding-top: ${ratio};`;
        }
      }

    }
</script>

<style scoped lang="scss">
    .outter {
        width: 100%;
        height: 0;
        position: relative;

        .inner {
            width: 100%;
            height: 100%;
            position: absolute;
            top: 0;
            left: 0;
        }
    }
</style>