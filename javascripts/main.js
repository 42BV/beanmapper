$(document).ready(function () {

    $('code').each(function(i, block) {
        hljs.highlightBlock(block);
    });
    var menu = $('#menu');
    menu.css('max-height', window.innerHeight);

    /**
     * Function to set fixed navigation after scrolling.
     */
    $(window).scroll(function () {
        var window_top = $(window).scrollTop();
        if(window_top > $('header').height()) {
            menu.addClass('stick');
        } else {
            menu.removeClass('stick');
        }
    });

    /**
     * Function that causes smooth scrolling.
     */
    menu.find("a").click(function () {
        var target = $(this.hash);
        $('html, body').animate({
            scrollTop: target.offset().top
        }, 750);
    });

    /**
     * Function that handles highlighting of menu items.
     */
    $(window).scroll(function () {
        var windowPos = $(window).scrollTop()+50; // Look 50px ahead

        menu.find("a").each(function() {
            var sectionByLink = $($(this).attr('href'));
            var divPos = sectionByLink.offset().top;
            var divHeight = sectionByLink.height();
            if (divPos <= windowPos && (divPos + divHeight) > windowPos) {
                $(this).addClass("active");
            } else {
                $(this).removeClass("active");
            }
        });
    });
});

$(window).resize(function() {
    $('#menu').css('max-height', window.innerHeight);
});