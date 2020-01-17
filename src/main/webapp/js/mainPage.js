var blogTotal;
var requestBlogTotalUrl;
var blogs;
var setTop = true;
var requestBlogsUrl;
var hotBlogs;
var tags;
var tagId;
var tagName;
var blogWrapper = $('.blog-wrapper').eq(0);

init();
function init() {
    requestBlogsUrl = '/allBlogs?pageIndex=';
    requestBlogTotalUrl = '/blogTotal';
    getBlogTotal();
    getBlogs();
    getHotBlogs();
    getTags();
    addSearchEvent();
}

function addSearchEvent() {
    $('.search-wrapper .icon').eq(0).off('click').on('click', function() {
        var keyWord = $('.search-wrapper .search').eq(0).val();
        requestBlogsUrl = '/search?keyWord=' + keyWord;
        setTop = false;
        getBlogs(requestBlogsUrl);
        clearIndex();
        hiddenPageIndex(true);
    });
}

function sendAjax(url, need) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.onreadystatechange = function (){
        if(xhr.readyState == 4 && xhr.status == 200) {
            if(need == 'blogs') {
                blogs = xhr.responseText;
                showBlogs(JSON.parse(blogs), setTop);
            }else if(need == 'blogTotal') {
                blogTotal = xhr.responseText;
                showPageIndex(parseInt(blogTotal));
            }else if(need == 'hotBlogs') {
                hotBlogs = xhr.responseText;
                showHotBlogsTitle(JSON.parse(hotBlogs));
            }else if(need == 'tags') {
                tags = xhr.responseText;
                showTags(JSON.parse(tags));
            }
        }
    }
    xhr.send();
}

function getBlogTotal() {
    sendAjax(requestBlogTotalUrl, 'blogTotal');
}

function getBlogs(theUrl) {
    var pageIndex = $('.active').eq(0);
    if(pageIndex == undefined || pageIndex.text() == '') pageIndex = 1;
    else pageIndex = pageIndex.text();
    var url;
    if(theUrl != undefined) url = theUrl;
    else url = requestBlogsUrl + pageIndex;
    sendAjax(url, 'blogs');
}

function getHotBlogs() {
    sendAjax('/hotBlogs', 'hotBlogs');
}

function getTags() {
    sendAjax('/allTags', 'tags');
}

function clearBlogs() {
    var blogs = $('.blog-wrapper .content');
    for(var i = 0; i < blogs.length; i ++) {
        if($(blogs[i]).hasClass('tpl')) continue;
        $(blogs[i]).remove();
    }
}

function clearIndex() {
    var index = $('.index');
    for(var i = 0; i < index.length; i ++) {
        $(index[i]).remove();
    }
}

function hiddenPageIndex(flag) {
    if(flag) $('.page-wrapper').eq(0).addClass('hidden');
    else $('.page-wrapper').eq(0).removeClass('hidden');
}

function showBlogs(blogsJSONArray, setTop) {
    clearBlogs();
    if(blogsJSONArray.length == 0) $('.no-blog').removeClass('hidden');
    else $('.no-blog').addClass('hidden');
    blogsJSONArray.forEach(function (ele, index) {
        var date = new Date(ele.ctime);
        var top = '';
        var time = date.getFullYear() + '/' + date.getMonth() + 1 + '/' + date.getDate() + ' ' + date.getHours() + '：' + date.getMinutes() + '：' + date.getSeconds();
        var theContent = $('.tpl').clone().removeClass('tpl');
        if(ele.isTop == 1 && setTop) {
            top = '[置顶]';
            theContent.find('.blog-title').addClass('is-top');
        }
        theContent
            .find('.blog-title')
                .eq(0)
                    .text(ele.blogTitle + top)
                        .attr('href', '/showBlog?blogId=' + ele.id);
        theContent.find('.author')
                    .text('作者：' + ele.author);
        theContent.find('.blog-content')
                    .eq(0)
                        .text(ele.contentHeader);
        theContent.find('.ctime')
                    .text('发布时间：' + time)
                        .next()
                            .next()
                                .text('阅读次数：' + ele.readCount)
                                    .next()
                                        .text('获赞数：' + ele.thumbs);
    var blogTags = theContent.find('.blog-tags');
    var tag = ele.tag;
    var tagArray = [];
    if(tag != undefined) tagArray = tag.split(',');
    else blogTags.remove();
    tagArray.forEach(function (tag, index) {
        $('<li class="tag hover">' + tag + '</li>').on('click', function (){
            tagName = this.textContent;
            requestBlogsUrl = '/tagBlogs?tagName=' + tagName + '&pageIndex=';
            requestBlogTotalUrl = '/blogTotalWithTag?tagName=' + tagName;
            setTop = false;
            clearIndex();
            getBlogTotal();
            getBlogs();
        }).appendTo(blogTags);
    });
    blogWrapper.append(theContent);
    });
}

function showPageIndex(blogTotal) {
    hiddenPageIndex(false);
    for(var i = 0; (i < 5) && (i < Math.ceil(blogTotal / 5)); i ++){  // 只展示 5 个页码
        $('.next').eq(0).before($('<li class="hover index">' + (i + 1) + '</li>'));
    }
    $('.index').eq(0).addClass('active');
    setCursorStyle();
    addLastPageEvent();
    addNextPageEvent();
    addIndexEvent();
}

function showTags(tagsJSONArray) {
    tagsJSONArray.forEach(function(tag, index) {
        $('<li class="content hover" tag-id="' + tag.id + '">' + tag.tagName + '</li>').on('click', function (){
            tagId = $(this).attr('tag-id');
            requestBlogsUrl = '/tagBlogs?tagId=' + tagId + '&pageIndex=';
            requestBlogTotalUrl = '/blogTotalWithTag?tagId=' + tagId;
            setTop = false;
            clearIndex();
            getBlogTotal();
            getBlogs();
        }).appendTo($('.tags-wrapper ul').eq(0));
    });
}

function showHotBlogsTitle(hotBlogsJSONArray) {
    hotBlogsJSONArray.forEach(function(blog, index) {
        $('<li class="content"><a class="hover" href="' + blog.contentUrl + '?blogId=' + blog.id + '" target="_blank">' + blog.blogTitle + '</a></li>').appendTo($('.hot-blog-wrapper ul').eq(0));
    });
    $('.hot-blogs').eq(0).off('click').on('click', function() {
        requestBlogsUrl = '/hotBlogs?pageIndex=';
        requestBlogTotalUrl = '/hotBlogsTotal';
        setTop = false;
        clearIndex();
        getBlogTotal();
        getBlogs();
    });
}

function canNext() {
    if($('.active').eq(0).text() == $('.index').eq(-1).text()) return false;
    else return true;
}

function canLast() {
    if($('.active').eq(0).text() == 1) return false;
    else return true;
}

function setCursorStyle() {
    if(canNext()) $('.next').eq(0).css({cursor: 'pointer'});
    else $('.next').eq(0).css({cursor: 'not-allowed'});

    if(canLast()) $('.last').eq(0).css({cursor: 'pointer'});
    else $('.last').eq(0).css({cursor: 'not-allowed'});
}

function changePageIndexContext(increment) {
    var pageIndex = $('.index');
    for(var i = 0; i < pageIndex.length; i ++) {
        var index = parseInt(pageIndex[i].textContent);
        $(pageIndex[i]).text(index + increment);
    }
}

function pageActiveChange(oldActive, newActive, boundary) {
    if(parseInt(oldActive.text()) == boundary) return;
    oldActive.removeClass('active');
    newActive.addClass('active');
}

function nextPage() {
    var pageTotal = Math.ceil(blogTotal / 5);
    if($('.index').eq(-1).text() == pageTotal || $('.active').eq(0).text() < Math.ceil(pageTotal / 2)) {
        var oldActive = $('.active').eq(0);
        var newActive = oldActive.next();
        pageActiveChange(oldActive, newActive, pageTotal);
    }else {
        changePageIndexContext(1);
    }
    setCursorStyle();
    getBlogs();
}

function addNextPageEvent() {
    $('.next').off('click').on('click', function(){
        nextPage();
    });
}

function lastPage() {
    var pageTotal = Math.ceil(blogTotal / 5);
    if($('.index').eq(0).text() == 1 || $('.active').eq(0).text() > Math.ceil(pageTotal / 2)) {
        var oldActive = $('.active').eq(0);
        var newActive = oldActive.prev();
        pageActiveChange(oldActive, newActive, 1);
    }else {
        changePageIndexContext(-1);
    }

    setCursorStyle();
    getBlogs();
}

function addLastPageEvent() {
    $('.last').off('click').on('click', function (){
        lastPage();
    });
}

function addIndexEvent() {
    var pageTotal = Math.ceil(blogTotal / 5);
    var indexArray = $('.index');
    for(var i = 0; i < indexArray.size(); i ++) {
        $(indexArray[i]).on('click', function() {
            var oldActive = $('.active').eq(0);
            var newActive = $(this);
            var clickText = newActive.text();
            var mid = $('.index').eq(parseInt($('.index').size() / 2));
            var firstIndex = $('.index').eq(0).text();
            var lastIndex = $('.index').eq(-1).text();
            if(clickText > mid.text() || (firstIndex != 1 && clickText < mid.text())) {
                if(newActive.text() < oldActive.text()) {
                    var increment = Math.min(oldActive.text() - newActive.text(), firstIndex - 1);
                    changePageIndexContext(-increment);
                    newActive = $('.index').eq(parseInt($('.index').size() / 2) - (parseInt(mid.text()) - parseInt(clickText)));
                }else {
                    var increment = Math.min(newActive.text() - oldActive.text(), pageTotal - lastIndex);
                    changePageIndexContext(increment);
                    newActive = $('.index').eq(parseInt($('.index').size() / 2) + parseInt(clickText) - parseInt(mid.text()));
                }
            }
            oldActive.removeClass('active');
            newActive.addClass('active');
            setCursorStyle();
            getBlogs();
        });
    }
}