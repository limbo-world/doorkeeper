
/**
 * 快速生成AsyncValidation的校验规则
 */

const Patterns = {
    Phone: /(13[0-9]|14[5-9]|15[0-3,5-9]|16[2,5-7]|17[0-8]|18[0-9]|19[0-3,5-9])\d{8}/,
    Email: /[a-zA-Z0-9_\.-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+/,
};

export default {

    required(filedName, type = 'input', trigger = 'blur') {
        const message = type === 'select' ? `请选择 ${filedName}` : `${filedName} 不可为空`;
        return {required: true, message, trigger};
    },

    length(min, max, trigger = 'blur') {
        return {min, max, message: `长度在 ${min} 到 ${max} 个字符`, trigger};
    },

    pattern(fieldName, pattern, trigger = 'blur') {
        if (Patterns[pattern]) {
            pattern = Patterns[pattern];
        }
        return { pattern, message: `${fieldName} 不符合规则`, trigger  };
    },


}
