$(document).ready(function () {
    var offsetFromTop = 100;
    
    $('code').each(function(i, block) {
        hljs.highlightBlock(block);
    });

    /**
     * Function that causes smooth scrolling.
     */
    $('.sidebar').find("a").click(function () {
        var target = $(this.hash);
        $('html, body').animate({
            scrollTop: target.offset().top - offsetFromTop
        }, 750);
    });

    /**
     * Function that handles highlighting of menu items.
     */
    $(window).scroll(function () {
        var windowPos = $(window).scrollTop() + offsetFromTop + 1;

        $('.sidebar').find("a").each(function() {
            var sectionByLink = $($(this).attr('href'));
            var divPos = sectionByLink.offset().top;
            var divHeight = sectionByLink.height();
            if (divPos <= windowPos && (divPos + divHeight) >= windowPos) {
                $(this).parent().addClass("active");
            } else {
                $(this).parent().removeClass("active");
            }
        });
    });
});