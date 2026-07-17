const PROGRESS_KEY = 'gvr9_progress';
const RATINGS_KEY = 'gvr9_ratings';

// Retrieve progress from localStorage (returns an array of 12 booleans)
function getProgress() {
    const data = localStorage.getItem(PROGRESS_KEY);
    if (data) {
        try {
            const arr = JSON.parse(data);
            if (Array.isArray(arr) && arr.length === 12) {
                return arr;
            }
        } catch (e) {
            console.error('Error parsing progress data:', e);
        }
    }
    // Default: all 12 units are incomplete (false)
    const defaultProgress = Array(12).fill(false);
    localStorage.setItem(PROGRESS_KEY, JSON.stringify(defaultProgress));
    return defaultProgress;
}

// Save progress of a specific unit (unitId is 1-indexed, 1-12)
function saveProgress(unitId, isCompleted) {
    if (unitId < 1 || unitId > 12) return;
    const progress = getProgress();
    progress[unitId - 1] = isCompleted;
    localStorage.setItem(PROGRESS_KEY, JSON.stringify(progress));
    
    // Custom event to notify other scripts of progress change (e.g. progress bar updates)
    window.dispatchEvent(new Event('progressUpdated'));
}

// Get count of completed units
function getCompletedCount() {
    const progress = getProgress();
    return progress.filter(Boolean).length;
}

// Reset all progress
function resetProgress() {
    const defaultProgress = Array(12).fill(false);
    localStorage.setItem(PROGRESS_KEY, JSON.stringify(defaultProgress));
    localStorage.removeItem(RATINGS_KEY); // Clear ratings as well when resetting progress
    window.dispatchEvent(new Event('progressUpdated'));
    window.dispatchEvent(new Event('ratingsUpdated'));
}

// Retrieve ratings from localStorage
function getRatings() {
    const data = localStorage.getItem(RATINGS_KEY);
    if (data) {
        try {
            return JSON.parse(data);
        } catch (e) {
            console.error('Error parsing ratings data:', e);
        }
    }
    return {};
}

// Save rating for a unit (unitId is 1-indexed, rating is 1-5, tags is array of strings, comment is string)
function saveRating(unitId, rating, tags = [], comment = '') {
    if (unitId < 1 || unitId > 12) return;
    const ratings = getRatings();
    ratings[unitId] = {
        rating: parseInt(rating, 10),
        tags: tags,
        comment: comment,
        date: new Date().toISOString()
    };
    localStorage.setItem(RATINGS_KEY, JSON.stringify(ratings));
    window.dispatchEvent(new Event('ratingsUpdated'));
}

// Get rating of a specific unit
function getUnitRating(unitId) {
    const ratings = getRatings();
    return ratings[unitId] || null;
}

// Dynamic Profile Level update based on completed units (0-4: Beginner, 5-8: Intermediate, 9-12: Expert)
function updateProfileLevelDOM() {
    const profileLevelEl = document.getElementById('profile-level');
    if (!profileLevelEl) return;
    
    const completedCount = getCompletedCount();
    let level = 'Beginner';
    if (completedCount >= 9) {
        level = 'Expert';
    } else if (completedCount >= 5) {
        level = 'Intermediate';
    }
    
    profileLevelEl.textContent = `Level: ${level}`;
}

// Expose functions globally
if (typeof window !== 'undefined') {
    window.getProgress = getProgress;
    window.saveProgress = saveProgress;
    window.getCompletedCount = getCompletedCount;
    window.resetProgress = resetProgress;
    window.getRatings = getRatings;
    window.saveRating = saveRating;
    window.getUnitRating = getUnitRating;
    window.updateProfileLevelDOM = updateProfileLevelDOM;

    // Run when DOM is ready
    if (document.readyState === 'loading') {
        window.addEventListener('DOMContentLoaded', updateProfileLevelDOM);
    } else {
        updateProfileLevelDOM();
    }
    
    // Run when progress changes
    window.addEventListener('progressUpdated', updateProfileLevelDOM);
}
