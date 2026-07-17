document.addEventListener('DOMContentLoaded', () => {
    setupStaggerAnimations();
    setupMobileTooltipToggle();
    setupSmoothScrolls();
});

// Stagger entry animations for elements with 'stagger-item'
function setupStaggerAnimations() {
    const grids = document.querySelectorAll('.features-grid, .units-grid, .vocab-grid');
    
    grids.forEach(grid => {
        const items = grid.children;
        Array.from(items).forEach((item, index) => {
            item.style.opacity = '0';
            item.style.transform = 'translateY(15px)';
            item.style.transition = 'opacity 0.6s cubic-bezier(0.16, 1, 0.3, 1), transform 0.6s cubic-bezier(0.16, 1, 0.3, 1)';
            
            // Set delay based on index
            setTimeout(() => {
                item.style.opacity = '1';
                item.style.transform = 'translateY(0)';
            }, 80 * index);
        });
    });
}

// Mobile compatibility for hover tooltips: tap toggles the tooltip
function setupMobileTooltipToggle() {
    document.addEventListener('click', (e) => {
        const isHighlight = e.target.classList.contains('vocab-highlight');
        
        if (isHighlight) {
            // Prevent standard click actions if any
            e.preventDefault();
            
            const wasActive = e.target.classList.contains('active-tooltip');
            
            // Close all active tooltips first
            document.querySelectorAll('.vocab-highlight').forEach(el => {
                el.classList.remove('active-tooltip');
            });
            
            // Toggle current
            if (!wasActive) {
                e.target.classList.add('active-tooltip');
            }
        } else {
            // Clicked outside: close all tooltips
            document.querySelectorAll('.vocab-highlight').forEach(el => {
                el.classList.remove('active-tooltip');
            });
        }
    });
}

// Smooth scroll animations for anchors
function setupSmoothScrolls() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href === '#') return;
            
            const target = document.querySelector(href);
            if (target) {
                e.preventDefault();
                target.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });
}
