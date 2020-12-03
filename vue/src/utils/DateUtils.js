
const dateInRange = (date, rangeStart, rangeEnd) => {
    return date.getTime() >= rangeStart.getTime() && date.getTime() <= rangeEnd.getTime();
};

const formatDateYMDHMS = (date) => {
    return `${formatDateYMD(date)} ${formatDateHMS(date)}`;
};

const formatDateYMD = (date) => {
    return `${date.getFullYear()}-${formatNumber2(date.getMonth() + 1)}-${formatNumber2(date.getDate())}`;
};

const formatDateHMS = (date) => {
    return `${formatNumber2(date.getHours())}:${formatNumber2(date.getMinutes())}:${formatNumber2(date.getSeconds())}`;
};

const parseDateYMD = (dateStr) => {
    return parseDateYMDHMS(dateStr + ' 00:00:00');
};

const parseDateHMS = (dateStr) => {
    return parseDateYMDHMS('2000-01-01 ' + dateStr);
};

const parseDateYMDHMS = (dateStr) => {
    let year = parseInt(dateStr.substr(0, 4));
    let month = parseInt(dateStr.substr(5, 2));
    let day = parseInt(dateStr.substr(8, 2));
    let hour = parseInt(dateStr.substr(11, 2));
    let min = parseInt(dateStr.substr(14, 2));
    let sec = parseInt(dateStr.substr(17, 2));
    return new Date(year, month - 1, day, hour, min, sec);
};

const addDay = (date, interval) => {
    date = typeof(date) === 'string'
        ? date.length === 10 ? parseDateYMD(date) : new Date(date)
        : date;
    let newDate = new Date(date.getTime());
    newDate.setDate(newDate.getDate() + 1);
    return newDate;
};

const isSameDay = (date1, date2) => {
    if (date1 === date2) {
        return true;
    }

    date1 = typeof date1 === 'string' ? new Date(date1) : date1;
    date2 = typeof date2 === 'string' ? new Date(date2) : date2;
    return date1.getFullYear() === date2.getFullYear()
        && date1.getMonth() === date2.getMonth()
        && date1.getDate() === date2.getDate();
};



const formatNumber2 = (n) => {
    n = n.toString();
    return n[1] ? n : '0' + n
};


export default {
    dateInRange,
    formatDateYMDHMS,
    formatDateYMD,
    formatDateHMS,
    parseDateYMDHMS,
    parseDateYMD,
    parseDateHMS,
    addDay,
    isSameDay
}
