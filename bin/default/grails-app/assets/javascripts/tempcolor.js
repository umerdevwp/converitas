function tempColor(temp/*, text*/) {
    const t = Math.round(32*temp);
    // console.log(temp+" -> "+t+" for "+text);
    switch (t) {
        case 32: return "#790402";
        case 31: return "#940d00";
        case 30: return "#c12201";
        case 29: return "#d23105";
        case 28: return "#e14008";
        case 27: return "#eb520e";
        case 26: return "#f56818";
        case 25: return "#fb8021";
        case 24: return "#fe972b";
        case 23: return "#fcac34";
        case 22: return "#f7c039";
        case 21: return "#ecd03a";
        case 20: return "#d2d332";
        case 19: return "#afce2c";
        case 18: return "#95cb2b";
        case 17: return "#9ad02b";
        case 16: return "#7fc52e";
        case 15: return "#63bd38";
        case 14: return "#55cb50";
        case 13: return "#3bc060";
        case 12: return "#29c980";
        case 11: return "#1ac595";
        case 10: return "#14b7a3";
        case  9: return "#19b1bd";
        case  8: return "#239ec7";
        case  7: return "#2f92da";
        case  6: return "#3a84e5";
        case  5: return "#437bef";
        case  4: return "#456be3";
        case  3: return "#4356c6";
        case  2: return "#382a72";
        default: return "#2b0536";
    }
}