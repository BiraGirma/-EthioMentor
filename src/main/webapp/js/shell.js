/*
 * EthioMentor app shell behaviour: desktop collapse + mobile navigation drawer.
 * Loaded on every authenticated page that includes WEB-INF/jsp/includes/sidebar.jsp.
 * Pages without the shell simply do nothing.
 */
(function () {
    'use strict';

    var layout = document.querySelector('.app-layout');
    var sidebar = document.getElementById('sidebar');
    var toggle = document.getElementById('navToggle');
    var collapseBtn = document.getElementById('collapseBtn');
    if (!layout || !sidebar) return;

    var desktop = window.matchMedia('(min-width: 901px)');
    var content = document.querySelector('.main-content');

    /* ---- Desktop collapse (remembered per browser) ---- */

    function readCollapsed() {
        try { return window.localStorage.getItem('sidebar-collapsed') === 'true'; }
        catch (e) { return false; }
    }

    function writeCollapsed(value) {
        try { window.localStorage.setItem('sidebar-collapsed', String(value)); }
        catch (e) { /* storage unavailable: the preference simply isn't remembered */ }
    }

    function applyCollapsed(collapsed) {
        layout.classList.toggle('nav-collapsed', collapsed);
        if (collapseBtn) {
            collapseBtn.setAttribute('aria-expanded', String(!collapsed));
            collapseBtn.setAttribute('aria-label', collapsed ? 'Expand navigation' : 'Collapse navigation');
        }
    }

    if (collapseBtn) {
        applyCollapsed(desktop.matches && readCollapsed());
        collapseBtn.addEventListener('click', function () {
            var collapsed = !layout.classList.contains('nav-collapsed');
            applyCollapsed(collapsed);
            writeCollapsed(collapsed);
        });
    }

    /* ---- Mobile drawer ---- */

    if (!toggle) return;

    var backdrop = document.createElement('div');
    backdrop.className = 'nav-backdrop';
    backdrop.setAttribute('aria-hidden', 'true');
    layout.appendChild(backdrop);

    function isOpen() {
        return layout.classList.contains('nav-open');
    }

    // A closed drawer on small screens is inert: not focusable and hidden from assistive tech.
    function syncInert(open) {
        sidebar.inert = !open && !desktop.matches;
    }

    function setOpen(open) {
        layout.classList.toggle('nav-open', open);
        syncInert(open);
        toggle.setAttribute('aria-expanded', String(open));
        document.body.classList.toggle('nav-locked', open);
        // Keep keyboard focus inside the drawer while it is open.
        if (content) content.inert = open;
        if (open) {
            var first = sidebar.querySelector('.sidebar-nav a');
            if (first) first.focus();
        }
    }

    toggle.addEventListener('click', function () {
        setOpen(!isOpen());
    });

    backdrop.addEventListener('click', function () {
        setOpen(false);
        toggle.focus();
    });

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && isOpen()) {
            setOpen(false);
            toggle.focus();
        }
    });

    // Following a link on a small screen closes the drawer before the page changes.
    sidebar.addEventListener('click', function (event) {
        if (!desktop.matches && event.target.closest('a')) setOpen(false);
    });

    // Moving to a wide screen with the drawer open resets it.
    desktop.addEventListener('change', function (event) {
        if (event.matches) sidebar.inert = false;
        if (event.matches && isOpen()) setOpen(false);
        if (!event.matches && !isOpen()) sidebar.inert = true;
    });

    toggle.setAttribute('aria-expanded', 'false');
    syncInert(false);
})();
