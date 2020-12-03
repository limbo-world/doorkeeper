const C_NANOS = 1;
const C_MICROS = C_NANOS * 1000;
const C_MILLIS = C_MICROS * 1000;
const C_SECONDS = C_MILLIS * 1000;
const C_MINUTES = C_SECONDS * 60;
const C_HOURS = C_MINUTES * 60;
const C_DAYS = C_HOURS * 24;

const MAX = Number.MAX_VALUE;
const MIN = Number.MIN_VALUE;

// 防止溢出
function x(d, m, over) {
    if (d > over) {
        return MAX;
    }
    if (d < -over) {
        return MIN;
    }
    return d * m;
}

// 纳秒
const Nanos = {
    name: 'Nanos',
    toNanos: function (d) {
        return d;
    },
    toMicros: function (d) {
        return d / (C_MICROS / C_NANOS);
    },
    toMillis: function (d) {
        return d / (C_MILLIS / C_NANOS);
    },
    toSeconds: function (d) {
        return d / (C_SECONDS / C_NANOS);
    },
    toMinutes: function (d) {
        return d / (C_MINUTES / C_NANOS);
    },
    toHours: function (d) {
        return d / (C_HOURS / C_NANOS);
    },
    toDays: function (d) {
        return d / (C_DAYS / C_NANOS);
    },
    convert: function (d, timeUnit) {
        return timeUnit.toNanos(d);
    },
};

// 微秒
const Micros = {
    name: 'Micros',
    toNanos: function (d) {
        const m = C_MICROS / C_NANOS;
        return x(d, m, MAX / m);
    },
    toMicros: function (d) {
        return d;
    },
    toMillis: function (d) {
        return d / (C_MILLIS / C_MICROS);
    },
    toSeconds: function (d) {
        return d / (C_SECONDS / C_MICROS);
    },
    toMinutes: function (d) {
        return d / (C_MINUTES / C_MICROS);
    },
    toHours: function (d) {
        return d / (C_HOURS / C_MICROS);
    },
    toDays: function (d) {
        return d / (C_DAYS / C_MICROS);
    },
    convert: function (d, timeUnit) {
        return timeUnit.toMicros(d);
    }
};

// 毫秒
const Millis = {
    name: 'Millis',
    toNanos: function (d) {
        const m = C_MILLIS / C_NANOS;
        return x(d, m, MAX / m);
    },
    toMicros: function (d) {
        const m = C_MILLIS / C_MICROS;
        return x(d, m, MAX / m);
    },
    toMillis: function (d) {
        return d;
    },
    toSeconds: function (d) {
        return d / (C_SECONDS / C_MILLIS);
    },
    toMinutes: function (d) {
        return d / (C_MINUTES / C_MILLIS);
    },
    toHours: function (d) {
        return d / (C_HOURS / C_MILLIS);
    },
    toDays: function (d) {
        return d / (C_DAYS / C_MILLIS);
    },
    convert: function (d, timeUnit) {
        return timeUnit.toMillis(d);
    }
};

// 秒
const Seconds = {
    name: 'Seconds',
    toNanos: function (d) {
        const m = C_SECONDS / C_NANOS;
        return x(d, m, MAX / m);
    },
    toMicros: function (d) {
        const m = C_SECONDS / C_MICROS;
        return x(d, m, MAX / m);
    },
    toMillis: function (d) {
        const m = C_SECONDS / C_MILLIS;
        return x(d, m, MAX / m);
    },
    toSeconds: function (d) {
        return d;
    },
    toMinutes: function (d) {
        return d / (C_MINUTES / C_SECONDS);
    },
    toHours: function (d) {
        return d / (C_HOURS / C_SECONDS);
    },
    toDays: function (d) {
        return d / (C_DAYS / C_SECONDS);
    },
    convert: function (d, timeUnit) {
        return timeUnit.toSeconds(d);
    }
};

// 分
const Minutes = {
    name: 'Minutes',
    toNanos: function (d) {
        const m = C_MINUTES / C_NANOS;
        return x(d, m, MAX / m);
    },
    toMicros: function (d) {
        const m = C_MINUTES / C_MICROS;
        return x(d, m, MAX / m);
    },
    toMillis: function (d) {
        const m = C_MINUTES / C_MILLIS;
        return x(d, m, MAX / m);
    },
    toSeconds: function (d) {
        const m = C_MINUTES / C_SECONDS;
        return x(d, m, MAX / m);
    },
    toMinutes: function (d) {
        return d;
    },
    toHours: function (d) {
        return d / (C_HOURS / C_MINUTES);
    },
    toDays: function (d) {
        return d / (C_DAYS / C_MINUTES);
    },
    convert: function (d, timeUnit) {
        return timeUnit.toMinutes(d);
    }
};

// 时
const Hours = {
    name: 'Hours',
    toNanos: function (d) {
        const m = C_HOURS / C_NANOS;
        return x(d, m, MAX / m);
    },
    toMicros: function (d) {
        const m = C_HOURS / C_MICROS;
        return x(d, m, MAX / m);
    },
    toMillis: function (d) {
        const m = C_HOURS / C_MILLIS;
        return x(d, m, MAX / m);
    },
    toSeconds: function (d) {
        const m = C_HOURS / C_SECONDS;
        return x(d, m, MAX / m);
    },
    toMinutes: function (d) {
        const m = C_HOURS / C_MINUTES;
        return x(d, m, MAX / m);
    },
    toHours: function (d) {
        return d;
    },
    toDays: function (d) {
        return d / (C_DAYS / C_HOURS);
    },
    convert: function (d, timeUnit) {
        return timeUnit.toHours(d);
    }
};

// 天
const Days = {
    name: 'Days',
    toNanos: function (d) {
        const m = C_DAYS / C_NANOS;
        return x(d, m, MAX / m);
    },
    toMicros: function (d) {
        const m = C_DAYS / C_MICROS;
        return x(d, m, MAX / m);
    },
    toMillis: function (d) {
        const m = C_DAYS / C_MILLIS;
        return x(d, m, MAX / m);
    },
    toSeconds: function (d) {
        const m = C_DAYS / C_SECONDS;
        return x(d, m, MAX / m);
    },
    toMinutes: function (d) {
        const m = C_DAYS / C_MINUTES;
        return x(d, m, MAX / m);
    },
    toHours: function (d) {
        const m = C_DAYS / C_HOURS;
        return x(d, m, MAX / m);
    },
    toDays: function (d) {
        return d;
    },
    convert: function (d, timeUnit) {
        return timeUnit.toDays(d);
    }
};


export default {
    Nanos, Micros, Millis,
    Seconds, Minutes, Hours, Days,
}
