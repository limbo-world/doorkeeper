<template>
    <el-container>
        <el-main>
            <el-form :model="realm" label-width="120px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称">
                    <el-input v-model="realm.name"></el-input>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>
    import { mapState } from 'vuex';
    import Rules from '../../utils/ValidateRules';

    export default {
        data() {
            return {
                realm: {}
            };
        },

        computed: {
            ...mapState('session', ['user']),
        },

        created() {
            pages.realAddEdit = this;
        },

        methods: {
            clearData() {
                this.realm = {}
            },
            addRealm() {
                return this.$ajax.post(`/admin/realm`, this.realm);
            },
        }
    }
</script>
