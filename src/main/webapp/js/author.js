var imgSuffixObj = {
    'JPG': true,
    'JPEG': true,
    'PNG': true,
    'GIF': true
}
var allTagsName = {};

init();
function init() {
    addNavEvent();
    addOperateEvent();
    sendAjax('/allTags');
}

function getAllTagsName(allTagsJSON) {
    for(var i = 0; i < allTagsJSON.length; i ++) {
        allTagsName[allTagsJSON[i].tagName] = true;
    }
}

function sendAjax(url) {
    var xhr = new XMLHttpRequest;
    xhr.open('POST', url, true);
    xhr.onreadystatechange = function () {
        if(xhr.readyState == 4 && xhr.status == 200) {
            if(url == '/allTags') {
                getAllTagsName(JSON.parse(xhr.responseText));
                return;
            }
            alert(xhr.responseText);
        }
    }
    xhr.send();
}

function sendAjaxWithFile(url, form) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.onreadystatechange = function (){
        if(xhr.readyState == 4 && xhr.status == 200) {
            alert(xhr.responseText);
        }
    }
    xhr.send(form);
}

// 先隐藏所有
function hiddenAll() {
    var operateOption = $('.wrapper div');
    for(var i = 0; i < operateOption.length; i ++) {
        $(operateOption[i]).addClass('hidden');
    }
    var input = $('input');
    for(var i = 0; i < input.length; i ++) {
        $(input[i]).val('');
    }
}

// 展示
function showInput(className, operate) {
    hiddenAll();
    $('.wrapper').eq(0).removeClass('hidden');
    $(className).eq(0).removeClass('hidden');
    $('.wrapper ' + className + ' .operate').eq(0).text(operate);
}

function addNavEvent() {
    $('.add-author').eq(0).off('click').on('click', function() {
        showInput('.operate-author-wrapper', $(this).text().substring(0, 2));
    });
    $('.delete-author').eq(0).off('click').on('click', function() {
        showInput('.operate-author-wrapper', $(this).text().substring(0, 2));
    });

    $('.add-blog').eq(0).off('click').on('click', function() {
        $('.id').eq(0).addClass('hidden');
        showInput('.operate-blog-wrapper', $(this).text().substring(0, 2));
    });
    $('.update-blog').eq(0).off('click').on('click', function() {
        $('.id').eq(0).removeClass('hidden');
        $('.operate-blog-wrapper .blog-html-file').eq(0).text('博客html文件(可有可无):');
        $('.operate-blog-wrapper .blog-css-file').eq(0).text('博客css文件(可有可无):');
        showInput('.operate-blog-wrapper', $(this).text().substring(0, 2));
    });
    $('.delete-the-blog').eq(0).off('click').on('click', function() {
        showInput('.delete-blog', $(this).text().substring(0, 2));
    });

    $('.add-tag').eq(0).off('click').on('click', function() {
        $('.tag-msg').eq(0).attr('placeholder', '标签名字');
        showInput('.tag-wrapper', $(this).text().substring(0, 2));
    });
    $('.delete-tag').eq(0).off('click').on('click', function() {
        $('.tag-msg').eq(0).attr('placeholder', '标签id');
        showInput('.tag-wrapper', $(this).text().substring(0, 2));
    });

    $('.delete-comment').eq(0).off('click').on('click', function() {
        showInput('.comment-wrapper', $(this).text().substring(0, 2));
    });

    $('.add-book').eq(0).off('click').on('click', function() {
        $('.book-id').eq(0).addClass('hidden');
        showInput('.book-wrapper', $(this).text().substring(0, 2));
    });
    $('.update-book').eq(0).off('click').on('click', function() {
        $('.book-id').eq(0).removeClass('hidden');
        $('.book-wrapper .book-file').eq(0).text('书籍图片(可有可无):');
        $('.book-wrapper .book-file').eq(0).css({left: '28%'});
        showInput('.book-wrapper', $(this).text().substring(0, 2));
    });
    $('.delete-the-book').eq(0).off('click').on('click', function() {
        showInput('.delete-book', $(this).text().substring(0, 2));
    });

}

// 判断填的标签是否都有
function havaTags(tagValue) {
    if('更新' == $('.blog-operate').eq(0).text()) return true;
    if(tagValue == '') {
        alert('标签不能为空');
        return false;
    }
    var tags = tagValue.split(',');
    for(var i = 0; i < tags.length; i ++) {
        if(allTagsName[tags[i]] == undefined) {
            alert('没有 ' + tags[i] + ' 这个标签');
            return false;
        }
    }
    return true;
}

function addOperateEvent() {

    // 添加/删除作者
    $('.author-operate').eq(0).off('click').on('click', function() {
        var authorName = $('.author-name').eq(0).val();
        var password = $('.password').eq(0).val();
        var urlParameter = '?authorName=' + authorName + '&password=' + password; 
        var url;
        if($(this).text() == '添加') url = '/addAuthor' + urlParameter;
        else url = '/deleteAuthor' + urlParameter;
        sendAjax(url);
    });
    // 添加/修改文章
    $('.blog-operate').eq(0).off('click').on('click', function (){
        var authorId = window.location.href.split('?')[1].split('=')[1];
        var blogTitle = $('.blog-title').eq(0).val();
        var contentHeader = $('.content-header').eq(0).val();
        var blogTag = $('.blog-tag').eq(0).val();
        var isTop = $('.is-top').eq(0).val();
        var htmlFile = $('.html-file').eq(0)[0].files[0];
        var cssFile = $('.css-file').eq(0)[0].files[0];
        var form = new FormData();

        if(!havaTags(blogTag)) return;
        blogTag = blogTag.replace('C++', 'CAA');

        form.append('authorId', authorId);
        form.append('blogTitle', blogTitle);
        form.append('contentHeader', contentHeader);
        form.append('blogTag', blogTag);
        form.append('isTop', isTop);
        if(htmlFile == undefined && '添加' == $('.blog-operate').eq(0).text()) {
            alert('html文件不能为空');
            return ;
        }
        if(htmlFile != undefined) form.append(htmlFile.name, htmlFile);
        if(cssFile != undefined) form.append(cssFile.name, cssFile);

        var url;
        if('添加' == $('.blog-operate').eq(0).text()) url = '/addBlog';
        else {
            form.append('blogId', $('.operate-blog-wrapper .id').eq(0).val());
            url = '/updateBlog';
        }
        sendAjaxWithFile(url, form);
    });

    // 删除文章
    $('.delete').eq(0).off('click').on('click', function() {
        var blogId = $('.blog-id').eq(0).val();
        var url = '/deleteBlog?blogId=' + blogId;
        sendAjax(url);
    });

    // 添加/删除标签
    $('.tag-operate').eq(0).off('click').on('click', function() {
        var param = $('.tag-msg').eq(0).val();
        var url;
        if($(this).text() == '添加') url = '/addTag?tagName=' + param;
        else url = '/deleteTag?tagId=' + param;
        sendAjax(url);
    });

    // 删除评论
    $('.comment-delete').eq(0).off('click').on('click', function() {
        var commentId = $('.comment-id').eq(0).val();
        var url = '/deleteComment?commentId=' + commentId;
        sendAjax(url);
    });

    // 添加/更新书籍
    $('.book-operate').eq(0).off('click').on('click', function() {
        var bookName = $('.book-name').eq(0).val();
        var authorName = $('.book-wrapper .author-name').eq(0).val();
        var version = $('.version').eq(0).val();
        var recommendReasons = $('.recommend-reasons').eq(0).val();
        var bookFile = $('.book-img-file').eq(0)[0].files[0];
        if(bookFile == undefined && '添加' == $('.book-operate').eq(0).text()) {
            alert('图片文件不能为空');
            return;
        }
        var form = new FormData();
        if(bookFile != undefined) {
            var fileName = bookFile.name;
            var suffix = fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase();
            if(imgSuffixObj[suffix] == undefined) {
                alert('文件格式不正确，只支持，jpg,jpeg,png,gif');
                return;
            }
            form.append(fileName + '.img', bookFile)
        }
        form.append('bookName', bookName);
        form.append('authorName', authorName);
        form.append('version', version);
        form.append('recommendReasons', recommendReasons);
        var url;
        if('添加' == $('.book-operate').eq(0).text()) url = '/addBook';
        else {
            form.append('bookId', $('.book-wrapper .book-id').eq(0).val());
            url = '/updateBook';
            console.log($('.book-wrapper .book-id').eq(0).val());
        }
        sendAjaxWithFile(url, form);
    });

    // 删除书籍
    $('.delete-book-operate').eq(0).off('click').on('click', function() {
        var bookId = $('.delete-book .book-id').eq(0).val();
        var url = '/deleteBook?bookId=' + bookId;
        sendAjax(url);
    });
}

