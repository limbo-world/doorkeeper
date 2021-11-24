import CryptoJS from "crypto-js";

const md5AndHex = (s) => {
    return CryptoJS.MD5(s).toString();
};

const md5AndSalt = (s) => {
    let salt = '' + parseInt(Math.random() * 99999999) + parseInt(Math.random() * 99999999);

    if (salt.length < 16) {
        for (let i = 0; i < 16 - salt.length; i++) {
            salt += "0";
        }
    }

    let input = md5AndHex(s + salt);
    let cs = "";
    for (let i = 0; i < 48; i += 3) {
        cs += input.charAt(i / 3 * 2);
        cs += salt.charAt(i / 3);
        cs += input.charAt(i / 3 * 2 + 1);
    }
    return cs;
};

export default {
    md5AndHex,
    md5AndSalt
};
