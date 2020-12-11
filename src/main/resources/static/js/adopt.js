function isMobile(){
    let ua = navigator.userAgent; //获取浏览器UA
    return ua.includes('Android') || ua.includes('Mobile')||ua.includes('iPhone');
}

function getHeaderHeight(){
    return isMobile()?160:60;
}

function getUploadFieldHeight(){
    return isMobile()?160:60;
}

function getFileItemHeight(){
    return isMobile()?160:60;
}

function getMarginTopHeight(){
    if(isMobile()){
        return 260;
    }else {
        return 100;
    }
}

function getTextSize(){
    return isMobile()?35:16;
}

function getBrandTextSize(){
    return isMobile()?70:32;
}

function getDescTextSize(){
    return getTextSize()*0.8;
}

function getIconSize(){
    return isMobile()?100:40;
}