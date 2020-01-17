var allBooks;

init();
function init() {
    getBooks();
}

function getBooks() {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/recommendBook', true);
    xhr.onreadystatechange = function (){
        if(xhr.readyState == 4 && xhr.status == 200) {
            allBooks = xhr.responseText;
            showBooks(JSON.parse(allBooks));
        }
    }
    xhr.send();
}
function showBooks(booksJSONArray) {
    var bookWrapper = $('.book-wrapper').eq(0);
    booksJSONArray.forEach(function(ele, index) {
        var recommend = ele.recommendReasons;
        if(recommend.length > 280) recommend = recommend.substring(0, 280) + '...';
        var theContent = $('.tpl').clone().removeClass('tpl');
        theContent
            .find('.book-img')
                .eq(0)
                    .attr('src', '/getImg?bookId=' + ele.id);
        theContent
            .find('.book-msg ul .author')
                .eq(0)
                    .text('作者：' + ele.authorName)
                        .next()
                            .text('版本：' + ele.version)
                                .next()
                                    .text('推荐：' + recommend);
        bookWrapper.append(theContent);
    });
}