<template>
    <el-container>
        <el-main>
            <el-form :model="realm" label-width="120px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="ID" required>
                    <span>{{realm.realmId}}</span>
                </el-form-item>
                <el-form-item label="名称" required>
                    <el-input v-model="realm.name"></el-input>
                </el-form-item>
                <el-form-item label="secret">
                    <el-input v-model="realm.secret"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="updateRealm" size="mini" icon="el-icon-refresh">保存</el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>
import {mapState, mapActions} from 'vuex';

export default {
    data() {
        return {
            realm: {},
        }
    },

    computed: {
        ...mapState('sessionAggregate', ['user']),
    },

    created() {
        pages.realm = this;

        this.loadRealm();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        loadRealm() {
            this.startProgress();
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}`).then(response => {
                this.realm = response.data;
            }).finally(() => this.stopProgress());
        },

        updateRealm() {
            const loading = this.$loading();
            this.$ajax.put(`/admin/realm/${this.realm.realmId}`, this.realm).then(response => {
                this.loadRealm();
            }).finally(() => loading.close());
        },
    }

}
</script>

<style lang="scss">
.project-page {
    .el-table {
        .cell {
            min-height: 22px;
        }
    }

    .edit-dialog {
        .el-dialog {
            min-width: 500px;
        }
    }
}
</style>
