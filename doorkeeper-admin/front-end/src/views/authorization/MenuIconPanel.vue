<template>
    <el-container>
        <el-main>
            <el-tabs type="border-card">
                <el-tab-pane label="ElementUI" class="icon-elementui">
                    <div v-for="i in elementUIIcon" :key="i" class="icon" >
                        <i :class="i"
                           @click="$emit('icon', i)"></i>
                        <span>{{i}}</span>
                    </div>
                </el-tab-pane>

                <el-tab-pane label="FontAwesome" class="icon-fontawesome">
                    <div v-for="i in fontAwesomeIcon" :key="i" class="icon" >
                        <i :class="'fa fa-' + i"
                           @click="$emit('icon', 'fa fa-' + i)"></i>
                        <span>{{i}}</span>
                    </div>
                </el-tab-pane>

                <el-tab-pane label="ColorUI" class="icon-colorui">
                    <div v-for="i in colorUIIcon" :key="i" class="icon" >
                        <i :class="i"
                           @click="$emit('icon', i)"></i>
                        <span>{{i}}</span>
                    </div>
                </el-tab-pane>

            </el-tabs>
        </el-main>
    </el-container>

</template>


<script>
    import { ElementUI, FontAwesome5, ColorUI } from './Icons';

    export default {
        data() {
            return {
                elementUIIcon: ElementUI,
                fontAwesomeIcon: FontAwesome5,
                colorUIIcon: ColorUI,

            }
        }
    }
</script>


<style lang="scss">
    .icon-elementui, .icon-fontawesome, .icon-colorui {
        display: flex;
        flex-wrap: wrap;

        .icon {
            width: 150px;
            height: 80px;
            text-align: center;
            line-height: 80px;
            font-size: 26px;
            cursor: pointer;
            position: relative;

            span {
                display: block;
                font-size: 10px;
                text-align: center;
                line-height: 10px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                position: absolute;
                bottom: 0;
                left: 0;
                right: 0;
            }
        }
    }
</style>
