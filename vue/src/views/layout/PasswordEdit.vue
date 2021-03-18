<template>
    <el-container>
        <el-main>
            <el-form :model="account" label-width="120px" size="mini" class="edit-form" :rules="rules" ref="editForm">
                <el-form-item label="旧密码" prop="oldPassword">
                    <el-input v-model="account.oldPassword" type="password"></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="account.newPassword" type="password"></el-input>
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input v-model="account.confirmPassword" type="password"></el-input>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>
import {mapState} from 'vuex';
import Rules from '../../utils/ValidateRules';

export default {
    data() {
        const confirmPassword = (rule, value, cb) => {
            if (this.account.newPassword !== value) {
                cb(new Error('两次输入密码不一致'));
            } else {
                cb();
            }
        };

        return {
            rules: {
                originalPassword: [Rules.required('旧密码')],
                newPassword: [Rules.required('新密码')],
                confirmPassword: [
                    Rules.required('确认密码'),
                    {
                        validator: confirmPassword,
                        trigger: 'blur'
                    }
                ],
            },
            account: {}
        };
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.passwordEdit = this;
    },

    methods: {
        updatePassword() {
            return new Promise((resolve, reject) => {
                this.$refs.editForm.validate(valid => {
                    if (!valid) {
                        reject();
                        return;
                    }

                    this.$ajax.put(`/api/session/change-password`, {
                        oldPassword: this.account.oldPassword,
                        newPassword: this.account.newPassword
                    }).then((response) => {
                        resolve(response);
                    }).catch(reject);
                });
            });
        },
    }
}
</script>
