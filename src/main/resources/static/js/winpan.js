function Stack(){
    var items=[];

    this.push=function(element){
        items.push(element);

    }
    this.pop=function(){
        return items.pop();

    }
    this.peek=function(){
        return items.peek();

    }

    this.isEmpty=function(){
        return items.length===0;

    }

    this.size=function(){
        return items.length;

    }

    this.clear=function (){
        items=[];

    }

    this.print=function(){
        return items.toString();
    }
}

function HashMap() {
    this.map = {};
}
HashMap.prototype = {
    put: function (key, value) {// 向Map中增加元素（key, value)
        this.map[key] = value;
    },
    get: function (key) { //获取指定Key的元素值Value，失败返回Null
        if (this.map.hasOwnProperty(key)) {
            return this.map[key];
        }
        return null;
    },
    remove: function (key) { // 删除指定Key的元素，成功返回True，失败返回False
        if (this.map.hasOwnProperty(key)) {
            return delete this.map[key];
        }
        return false;
    },
    removeAll: function () {  //清空HashMap所有元素
        this.map = {};
    },
    keySet: function () { //获取Map中所有KEY的数组（Array）
        var _keys = [];
        for (var i in this.map) {
            _keys.push(i);
        }
        return _keys;
    }
};
HashMap.prototype.constructor = HashMap;



function formatDate(timestamp){
    return new Date(timestamp).toLocaleString();
}

function formatFileSize(len) {
    if(len<1024){
        return len+'B';
    }else if(len<1024*1024){
        return (len/1024.0).toFixed(2)+'KB';
    }else if(len<1024*1024*1024){
        return (len/1024.0/1024.0).toFixed(2)+'MB';
    }else if(len<1024*1024*1024*1024.0){
        return (len/1024.0/1024.0/1024.0).toFixed(2)+'GB';
    }
}



function initIcon() {
    let iconMap=new HashMap();
    iconMap.put('dir',"img/type/dir.png");

    iconMap.put('png',"img/type/png.png");
    iconMap.put('jpg',"img/type/jpg.png");
    iconMap.put('jpeg',"img/type/jpg.png");
    iconMap.put('webp',"img/type/img.png");
    iconMap.put('ttf',"img/type/img.png");

    iconMap.put('mp3',"img/type/mp3.png");
    iconMap.put('flac',"img/type/fla.png");
    iconMap.put('ape',"img/type/aac.png");
    iconMap.put('m4a',"img/type/mp3.png");

    iconMap.put('zip',"img/type/zip.png");
    iconMap.put('rar',"img/type/zip.png");
    iconMap.put('7z',"img/type/zip.png");
    iconMap.put('gz',"img/type/zip.png");

    iconMap.put('pdf',"img/type/pdf.png");
    iconMap.put('doc',"img/type/doc.png");
    iconMap.put('docx',"img/type/doc.png");
    iconMap.put('xls',"img/type/xls.png");
    iconMap.put('xlsx',"img/type/xls.png");
    iconMap.put('ppt',"img/type/ppt.png");
    iconMap.put('pptx',"img/type/xls.png");

    iconMap.put('exe',"img/type/exe.png")
    return iconMap;
}

function fileFormatIcon(type){
    return 'img/type/'+type+'.png';
}
function backToDomain(){
    window.location.href='/';
}

function parseRealKey(key){
    let search=location.search;
    return getSearchString(key,search);
}

//key(需要检索的键） url（传入的需要分割的url地址，例：?id=2&age=18）
function getSearchString(key, Url) {
    let str = Url;
    str = str.substring(1, str.length); // 获取URL中?之后的字符（去掉第一位的问号）
    let arr = str.split("&");
    let obj = {};
    // 将每一个数组元素以=分隔并赋给obj对象
    for (let i = 0; i < arr.length; i++) {
        let tmp_arr = arr[i].split("=");
        obj[decodeURIComponent(tmp_arr[0])] = decodeURIComponent(tmp_arr[1]);
    }
    return obj[key];
}

function validEmail(email) {
    let reg = new RegExp(/^\S+@\S+\.\S{2,}$/);
    return reg.test(email);
}

function isChar(c){
    return(c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');

}

function isNumber(c) {
    return (c >= '0' && c <= '9');
}

function validUserId(id) {
    if(id==null||id.length<=6||!isChar(id[0])){
        return false;
    }
    for(let c in id){
        if(!isChar(c)&&!isNumber(c)){
            return false;
        }
    }
    return true;
}

function regNewAccount(userId,password,email,userName,fun){
    let dat=new FormData();
    dat.append('userId',userId);
    dat.append('password',password);
    dat.append('email',email);
    dat.append('userName',userName);
    $.ajax({
        url:"/api/reg",
        type: 'POST',
        cache: false,
        data: dat,
        async:true,
        processData: false,
        contentType: false,
        success:function (params) {
            fun(params);
        }
    });
}

function getCookie(cookieName) {
    let allCookies = document.cookie;
    //索引长度，开始索引的位置
    let cookie_pos = allCookies.indexOf(cookieName);

    // 如果找到了索引，就代表cookie存在,否则不存在
    if (cookie_pos !== -1) {
        // 把cookie_pos放在值的开始，只要给值加1即可
        //计算取cookie值得开始索引，加的1为“=”
        cookie_pos = cookie_pos + cookieName.length + 1;
        //计算取cookie值得结束索引
        let cookie_end = allCookies.indexOf(";", cookie_pos);

        if (cookie_end === -1) {
            cookie_end = allCookies.length;
        }
        //得到想要的cookie的值
        return unescape(allCookies.substring(cookie_pos, cookie_end));
    }
    return null;
}
function utf8ToUtf16(str) {
    let out, i, len, c;
    let char2, char3;
    out = "";
    len = str.length;
    i = 0;
    while(i < len) {
        c = str.charCodeAt(i++);
        switch(c >> 4)
        {
            case 0: case 1: case 2: case 3: case 4: case 5: case 6:case 7:
            // 0xxxxxxx
            out += str.charAt(i-1);
            break;
            case 12: case 13:
            // 110x xxxx 10xx xxxx
            char2 = str.charCodeAt(i++);
            out += String.fromCharCode(((c & 0x1F) << 6) | (char2&0x3F));
            break;
            case 14:
                // 1110 xxxx 10xx xxxx 10xx xxxx
                char2 = str.charCodeAt(i++);
                char3 = str.charCodeAt(i++);
                out += String.fromCharCode(((c & 0x0F) << 12) |
                    ((char2 & 0x3F) << 6) |
                    ((char3 & 0x3F) << 0));
                break;
        }
    }
    return out;
}







