function timeConverter(UNIX_timestamp, mode){
    var a = new Date(UNIX_timestamp);
    var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    var year = a.getFullYear();
    var month = months[a.getMonth()];
    var date = a.getDate();
    var hour = a.getHours();
    var min = a.getMinutes();
    var sec = a.getSeconds();
    switch(mode) {
        case undefined: return date + ' ' + month + ' ' + year ;
        case 1: return date + ' ' + month + ' ' + year + ' ' + hour + ':' + min + ':' + sec;
        case 2: return date + ' ' + month + ' ' + year + ' ' + hour + ':' + min + ':' + sec + ' ' + getTimezoneName();
    }
}

function getTimezoneName() {
    const today = new Date();
    const short = today.toLocaleDateString(undefined);
    const full = today.toLocaleDateString(undefined, { timeZoneName: 'long' });

    // Trying to remove date from the string in a locale-agnostic way
    const shortIndex = full.indexOf(short);
    if (shortIndex >= 0) {
        const trimmed = full.substring(0, shortIndex) + full.substring(shortIndex + short.length);

        // by this time `trimmed` should be the timezone's name with some punctuation -
        // trim it from both sides
        return trimmed.replace(/^[\s,.\-:;]+|[\s,.\-:;]+$/g, '');

    } else {
        // in some magic case when short representation of date is not present in the long one, just return the long one as a fallback, since it should contain the timezone's name
        return full;
    }
}
