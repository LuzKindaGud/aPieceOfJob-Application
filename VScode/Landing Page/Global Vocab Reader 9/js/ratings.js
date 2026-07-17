// Ratings logic for Global Success 9 Learning Suite

document.addEventListener('DOMContentLoaded', () => {
    // Only run on unit details page
    if (!window.location.pathname.includes('unit.html')) return;
    
    initRatings();
});

let currentRatingValue = 0;
let selectedTags = [];

function initRatings() {
    const params = new URLSearchParams(window.location.search);
    const unitId = parseInt(params.get('unit'), 10) || 1;

    // Elements
    const ratingSummaryContainer = document.getElementById('rating-summary-container');
    const overallRatingDisplay = document.getElementById('overall-rating-display');
    const btnOpenFeedback = document.getElementById('btn-open-feedback');
    const feedbackModal = document.getElementById('feedback-modal');
    const modalClose = document.getElementById('modal-close');
    const btnCancelFeedback = document.getElementById('btn-cancel-feedback');
    const btnSubmitFeedback = document.getElementById('btn-submit-feedback');
    const starSelectContainer = document.getElementById('star-rating-select');
    const starBtns = starSelectContainer ? starSelectContainer.querySelectorAll('.star-btn') : [];
    const starDescription = document.getElementById('star-description');
    const feedbackComment = document.getElementById('feedback-comment');
    const tagsContainer = document.getElementById('feedback-tags-container');
    
    if (!ratingSummaryContainer || !btnOpenFeedback || !feedbackModal) return;

    // Star feedback mapping
    const starDescriptions = {
        1: "Difficult 😟",
        2: "Needs improvement 😕",
        3: "Okay 😐",
        4: "Good & Clear 🙂",
        5: "Excellent, love it! 😍"
    };

    // Load initial rating from localStorage
    function updateRatingSummary() {
        const savedRating = typeof window.getUnitRating === 'function' ? window.getUnitRating(unitId) : null;
        
        if (savedRating) {
            // Render existing rating
            overallRatingDisplay.innerHTML = `
                ${savedRating.rating} 
                <span class="text-yellow-500 material-symbols-outlined text-[28px]" style="font-variation-settings: 'FILL' 1;">star</span>
            `;
            btnOpenFeedback.innerHTML = `
                <span class="material-symbols-outlined">edit_note</span>
                <span>Change Rating</span>
            `;
            btnOpenFeedback.classList.remove('bg-primary');
            btnOpenFeedback.classList.add('bg-secondary');
        } else {
            // Unrated state
            overallRatingDisplay.innerHTML = `
                -
                <span class="text-outline-variant material-symbols-outlined text-[28px]">star</span>
            `;
            btnOpenFeedback.innerHTML = `
                <span class="material-symbols-outlined">rate_review</span>
                <span>Rate Lesson</span>
            `;
            btnOpenFeedback.classList.add('bg-primary');
            btnOpenFeedback.classList.remove('bg-secondary');
        }
    }

    // Modal control
    function openModal() {
        // Reset or populate inputs from localStorage
        const savedRating = typeof window.getUnitRating === 'function' ? window.getUnitRating(unitId) : null;
        
        if (savedRating) {
            setStarRating(savedRating.rating);
            selectedTags = [...savedRating.tags];
            feedbackComment.value = savedRating.comment;
            
            // Highlight selected tags
            if (tagsContainer) {
                tagsContainer.querySelectorAll('.tag-pill').forEach(pill => {
                    const tag = pill.getAttribute('data-tag');
                    if (selectedTags.includes(tag)) {
                        pill.classList.add('bg-[#bc0b36]/10', 'text-[#bc0b36]', 'border-[#bc0b36]');
                    } else {
                        pill.classList.remove('bg-[#bc0b36]/10', 'text-[#bc0b36]', 'border-[#bc0b36]');
                    }
                });
            }
        } else {
            // Reset to defaults
            setStarRating(0);
            selectedTags = [];
            feedbackComment.value = '';
            if (tagsContainer) {
                tagsContainer.querySelectorAll('.tag-pill').forEach(pill => {
                    pill.classList.remove('bg-[#bc0b36]/10', 'text-[#bc0b36]', 'border-[#bc0b36]');
                });
            }
        }
        
        feedbackModal.classList.remove('pointer-events-none', 'opacity-0');
        feedbackModal.querySelector('.transform').classList.remove('scale-95');
        feedbackModal.querySelector('.transform').classList.add('scale-100');
    }

    function closeModal() {
        feedbackModal.classList.add('pointer-events-none', 'opacity-0');
        feedbackModal.querySelector('.transform').classList.add('scale-95');
        feedbackModal.querySelector('.transform').classList.remove('scale-100');
    }

    // Stars interaction
    starBtns.forEach(btn => {
        const val = parseInt(btn.getAttribute('data-value'), 10);
        
        // Hover effects
        btn.addEventListener('mouseenter', () => {
            highlightStars(val);
            if (starDescription) starDescription.textContent = starDescriptions[val] || '';
        });
        
        btn.addEventListener('mouseleave', () => {
            highlightStars(currentRatingValue);
            if (starDescription) {
                starDescription.textContent = currentRatingValue > 0 ? starDescriptions[currentRatingValue] : 'Select stars';
            }
        });

        // Click to select
        btn.addEventListener('click', () => {
            setStarRating(val);
        });
    });

    function setStarRating(rating) {
        currentRatingValue = rating;
        highlightStars(rating);
        if (starDescription) {
            starDescription.textContent = rating > 0 ? starDescriptions[rating] : 'Select stars';
        }
    }

    function highlightStars(count) {
        starBtns.forEach((btn, index) => {
            const icon = btn.querySelector('span');
            if (index < count) {
                icon.style.fontVariationSettings = "'FILL' 1";
                icon.classList.remove('text-outline-variant');
                icon.classList.add('text-yellow-500');
            } else {
                icon.style.fontVariationSettings = "'FILL' 0";
                icon.classList.remove('text-yellow-500');
                icon.classList.add('text-outline-variant');
            }
        });
    }

    // Tags interaction
    if (tagsContainer) {
        tagsContainer.querySelectorAll('.tag-pill').forEach(pill => {
            pill.addEventListener('click', () => {
                const tag = pill.getAttribute('data-tag');
                const idx = selectedTags.indexOf(tag);
                
                if (idx > -1) {
                    // Remove tag
                    selectedTags.splice(idx, 1);
                    pill.classList.remove('bg-[#bc0b36]/10', 'text-[#bc0b36]', 'border-[#bc0b36]');
                } else {
                    // Add tag
                    selectedTags.push(tag);
                    pill.classList.add('bg-[#bc0b36]/10', 'text-[#bc0b36]', 'border-[#bc0b36]');
                }
            });
        });
    }

    // Submit rating
    btnSubmitFeedback.addEventListener('click', () => {
        if (currentRatingValue === 0) {
            alert('Please select a star rating (from 1-5 stars)!');
            return;
        }

        const comment = feedbackComment.value.trim();
        
        if (typeof window.saveRating === 'function') {
            window.saveRating(unitId, currentRatingValue, selectedTags, comment);
        }

        // Show thank you alert & close
        showSuccessNotification();
        closeModal();
        updateRatingSummary();
    });

    // Custom success notification
    function showSuccessNotification() {
        const container = document.createElement('div');
        container.className = 'fixed bottom-20 left-1/2 transform -translate-x-1/2 z-[110] bg-green-600 text-white px-6 py-3 rounded-full font-bold shadow-lg flex items-center gap-2 animate-bounce';
        container.innerHTML = `
            <span class="material-symbols-outlined">check_circle</span>
            <span>Thank you for your feedback!</span>
        `;
        document.body.appendChild(container);
        setTimeout(() => {
            container.classList.add('opacity-0', 'transition-opacity', 'duration-500');
            setTimeout(() => container.remove(), 500);
        }, 2500);
    }

    // Event listeners
    btnOpenFeedback.addEventListener('click', openModal);
    modalClose.addEventListener('click', closeModal);
    btnCancelFeedback.addEventListener('click', closeModal);

    // Close on click outside modal content
    feedbackModal.addEventListener('click', (e) => {
        if (e.target === feedbackModal) closeModal();
    });

    // Auto trigger modal callback
    window.triggerAutoRatingModal = function() {
        const savedRating = typeof window.getUnitRating === 'function' ? window.getUnitRating(unitId) : null;
        // Only trigger automatically if the user hasn't rated this unit yet
        if (!savedRating) {
            setTimeout(() => {
                openModal();
            }, 1000); // Trigger after 1 second of exercise completion for better UX
        }
    };

    // Initial load
    updateRatingSummary();
}
