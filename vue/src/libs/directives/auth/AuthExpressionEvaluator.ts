/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 权限表达式计算器
 * 权限表达式，在解析后会被翻译为一个bool表达式，由子表达式和操作符组成
 *
 * 子表达式：子表达式用花括号“{}”括起来，格式为“{鉴权类型.鉴权对象名}”，支持的鉴权类型有角色(role)、权限(perm)
 * 例如：
 *      {role.A} 表示角色A
 *      {perm.B} 表示角色B
 *
 * 操作符：普通bool操作符均可用，支持的操作符有：与(&)、或(|)、非(!)、短路与(&&)、短路或(||)、圆括号(())
 * 例如：
 *      {role.A} || ({perm.AA} && {perm.BB})
 *      表示 有角色A 或 同时有权限AA和权限BB时 才有权读取菜单
 *
 * 省略花括号的简写形式：如果权限表达式比较简单，没有操作符的话，可以将子表达式的花括号省略，直接使用“鉴权类型.鉴权对象名”的格式，在判断表达式时会认为其在花括号内
 * 例如：role.A 与 {role.A} 的表现结果一致
 *
 * 省略鉴权类型的简写形式：如果权限表达式内的只使用了一种鉴权类型，可以省略子表达式内的鉴权类型，在表达式头部添加“鉴权类型:”来简写
 * 例如：role:{A} && {B} 与 {role.A} && {role.B} 的表现结果一致
 * 如果简写鉴权类型的同时又在子表达式中指定了鉴权类型，则以子表达式中的鉴权类型为准
 * 例如：role:{A} || {B} || {C} || {perm.D} 与 {role.A} || {role.B} || {role.C} || {perm:D} 的表现结果一致
 *
 * 保留值：true、false，与bool值含义相同，在权限表达式中使用true或false与其bool值相同
 * 例如：权限表达式为 “true”，表示永远有权限
 *
 * 默认值：true
 */
export default class AuthExpressionEvaluator {

    private readonly roles: Set<string>;
    private readonly permissions: Set<string>;
    private readonly isAdmin: boolean = false;

    /**
     * 表达式关键字
     */
    private static KeyChars = new Set([':', '.', '{', '}']);

    constructor(roles: string[], permissions: string[], isAdmin: boolean) {
        this.roles = new Set(roles || []);
        this.permissions = new Set(permissions || []);
        this.isAdmin = isAdmin;
    }

    /**
     *
     * @param expression
     * @param defaultResult
     */
    evaluate(expression: string, defaultResult: boolean = true): boolean {
        if (this.isAdmin) {
            return true;
        }

        return eval(this.parseExpression(expression, defaultResult.toString()));
    }

    /**
     *
     * @param expression
     * @param defaultResult
     */
    private parseExpression(expression: string, defaultResult: string = "true"): string {
        // 如果表达式为空，
        if (!expression || expression.trim() === '') {
            return defaultResult.toString();
        }
        expression = expression.trim();

        // 如果简写了{}
        if (expression.indexOf("{") < 0 && expression.indexOf("}") < 0) {
            expression = "{" + expression + "}";
        }

        // 生成解析上下文
        const context = new ParseContext(this.roles, this.permissions, expression);
        for (let i = 0; i < expression.length; i++) {
            let c = expression.charAt(i);
            if (!context.parseState[c]) {
                // 非期待字符，但是是关键字的话，需要抛出异常
                if (AuthExpressionEvaluator.KeyChars.has(c)) {
                    throw AuthExpressionEvaluator.error(Object.keys(context.parseState).join(','), c);
                }

                // 非关键字，非期待字符，读取缓存起来
                context.readChar(c);
            } else {
                // 是期待字符，执行对应逻辑
                // TODO 这里是JS的函数调用方式，需要处理为TS
                context.parseState[c](context);
            }

            console.debug(`read [${c}], after parsed: ${context.parsedExpression}`)
        }

        console.debug(`origin expression: ${expression} \nparsed expression: ${context.parsedExpression}`)
        return context.parsedExpression;
    }

    private static error(expecting: string, got: any = null): Error {
        return new Error(`Parse auth expression error, expecting '${expecting}', but got ${got ? got : 'nothing'}`);
    }

}

class ParseContext {

    /**
     * 已授权的角色
     */
    private readonly roles: Set<string>;

    /**
     * 已授权的权限
     */
    private readonly permissions: Set<string>;

    /**
     * 当前解析状态
     */
    parseState: any;

    /**
     * 是否在表达式指定了简写的授权类型
     */
    signedAuthType?: string = undefined;

    /**
     * 子表达式中指定的授权类型
     */
    subExpressionAuthType?: string = undefined;

    /**
     * 原始表达式
     */
    originExpression: string;

    /**
     * 缓存已读取的字符
     */
    readCharsCache: string[];

    /**
     * 解析过的表达式
     */
    parsedExpression: string;


    constructor(roles: Set<string>, permissions: Set<string>, originExpression: string) {
        this.parseState = Idle;
        this.roles = roles;
        this.permissions = permissions;
        this.originExpression = originExpression;
        this.readCharsCache = [];
        this.parsedExpression = '';
    }

    /**
     * 读取一个字符
     */
    public readChar(c: string): void {
        this.readCharsCache.push(c);
    }

    /**
     * 获取已读的字符缓存
     */
    public retainReadChars():string {
        let str = this.readCharsCache.join('');
        this.readCharsCache = [];
        return str;
    }

    /**
     * 开始一个子表达式的解析
     */
    public startSubExpression() {
        // 开始新表达式之前，将缓存的字符写入已解析表达式
        if (this.readCharsCache.length > 0) {
            this.parsedExpression += this.readCharsCache.join('');
        }

        this.readCharsCache = [];
        this.subExpressionAuthType = undefined;
    }

    /**
     * 设置全局表达式授权类型
     */
    public set authType(authType: string) {
        if (this.signedAuthType != null) {
            throw new Error('Already sign auth type [' + this.signedAuthType + ']');
        }
        this.signedAuthType = authType;
    }


    /**
     * 设置子表达式授权类型
     */
    public set subAuthType(authType: string) {
        if (this.subExpressionAuthType != null) {
            throw new Error('Already sign auth type [' + this.subExpressionAuthType + ']');
        }
        this.subExpressionAuthType = authType;
    }

    /**
     * 结束子表达式解析
     */
    public endSubExpression() {
        // 检查鉴权类型
        const at = this.subExpressionAuthType == null ? this.signedAuthType : this.subExpressionAuthType;
        if (!at || at === '') {
            throw new Error("Cannot confirm auth type, did you forget 'authType:' at the start of expression?");
        }
        AuthType.validate(at);

        // 检查鉴权对象
        const authObject = this.retainReadChars();
        if (!authObject || authObject === '') {
            throw new Error("Cannot confirm auth object!");
        }

        // 检查鉴权结果
        const authObjectsSet: Set<string> = AuthType.Role.is(at) ? this.roles : this.permissions;
        if (authObjectsSet.has(authObject)) {
            this.parsedExpression += 'true';
        } else {
            this.parsedExpression += 'false';
        }
    }
}

/**
 * 鉴权类型
 */
class AuthType {

    static Role = new AuthType('role');
    static Permission = new AuthType('perm');

    private readonly type: string = "";

    constructor(type: string) {
        this.type = type;
    }

    /**
     * 是否是指定的授权类型
     */
    public is(authType: string): boolean {
        return authType != null && authType.toLowerCase() === this.type;
    }

    /**
     * 校验鉴权类型是否合法
     */
    public static validate(authType: string) {
        if (!this.Role.is(authType) && !this.Permission.is(authType)) {
            throw new Error("Illegal auth type '" + authType + "'!")
        }
    }

}

/**
 * 初始状态，未读取任何字符
 */
const Idle = {
    /**
     * 需要开始读取新的子表达式
     */
    '{': (ctx: ParseContext) => {
        ctx.startSubExpression();
        ctx.parseState = LeftRead;
    },

    /**
     * 需要设置简写的鉴权类型
     * @param ctx
     */
    ':': (ctx: ParseContext) => {
        let authType = ctx.retainReadChars();
        AuthType.validate(authType);

        ctx.authType = authType;
        ctx.parseState = Idle;
    },
}

/**
 * 左括号已读
 */
const LeftRead = {
    /**
     * 遇到“.”需要解析鉴权类型
     */
    '.': (ctx: ParseContext, c: string) => {
        let authType = ctx.retainReadChars();
        AuthType.validate(authType);

        ctx.subAuthType = authType;
        ctx.parseState = LeftRead;
    },

    /**
     * 遇到“}”说明子表达式完结
     */
    '}': (ctx: ParseContext) => {
        ctx.endSubExpression();
        ctx.parseState = RightRead;
    },
}

/**
 * 右括号已读
 */
const RightRead = {
    /**
     * 需要开始读取新的子表达式
     */
    '{': (ctx: ParseContext) => {
        ctx.startSubExpression();
        ctx.parseState = LeftRead;
    }
}
