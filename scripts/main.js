$(document).ready(function () {
    $('code').each(function(i, block) {
        hljs.highlightBlock(block);
    });

    /**
     * Function that causes smooth scrolling.
     */
    $('.sidebar').find("a").click(function () {
        var target = $(this.hash);
        $('html, body').animate({
            scrollTop: target.offset().top-50
        }, 750);
    });

    /**
     * Function that handles highlighting of menu items.
     */
    $(window).scroll(function () {
        var windowPos = $(window).scrollTop()+50; // Look 50px ahead

        $('.sidebar').find("a").each(function() {
            var sectionByLink = $($(this).attr('href'));
            var divPos = sectionByLink.offset().top;
            var divHeight = sectionByLink.height();
            if (divPos <= windowPos && (divPos + divHeight) > windowPos) {
                $(this).parent().addClass("active");
            } else {
                $(this).parent().removeClass("active");
            }
        });
    });
});