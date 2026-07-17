document.addEventListener('DOMContentLoaded', () => {
    // Detect which page we are on
    const path = window.location.pathname;
    const isUnitDetailPage = path.includes('unit.html');

    if (isUnitDetailPage) {
        loadUnitDetails();
    }
});

function loadUnitDetails() {
    const params = new URLSearchParams(window.location.search);
    const unitIdStr = params.get('unit');
    const unitId = parseInt(unitIdStr, 10);

    // Validate Unit ID
    if (isNaN(unitId) || unitId < 1 || unitId > 12) {
        console.warn('Invalid unit ID, redirecting...');
        window.location.href = 'units.html';
        return;
    }

    // Retrieve Unit Data from global window.UNITS_DATA
    const unitData = window.UNITS_DATA.find(u => u.id === unitId);
    if (!unitData) {
        console.error('Unit data not found!');
        window.location.href = 'units.html';
        return;
    }

    // 1. Update Title and Headers
    document.title = `GVR9 — ${unitData.title}`;
    
    const unitTitleEl = document.getElementById('unit-title');
    if (unitTitleEl) {
        unitTitleEl.textContent = unitData.title;
    }

    const unitTopicEl = document.getElementById('unit-topic');
    if (unitTopicEl) {
        unitTopicEl.textContent = `Topic: ${unitData.topic}`;
    }

    // 2. Render Reading Block
    const readingBlockEl = document.getElementById('reading-block');
    if (readingBlockEl) {
        readingBlockEl.innerHTML = unitData.reading.content;
    }

    // 3. Render Vocabulary cards
    const vocabGridEl = document.getElementById('vocab-grid');
    if (vocabGridEl) {
        vocabGridEl.innerHTML = ''; // clear
        unitData.vocabulary.forEach(vocab => {
            const card = document.createElement('div');
            card.className = 'vocab-card flex flex-col sm:flex-row gap-4 items-start bg-white p-6 rounded-xl border border-outline-variant/30 shadow-sm hover:shadow-md transition-all duration-200';
            
            const imgHtml = vocab.image ? `
                <div class="w-full sm:w-24 h-24 rounded-lg overflow-hidden flex-shrink-0 bg-surface-container-highest border border-outline-variant/30">
                    <img class="w-full h-full object-cover" src="${vocab.image}" alt="${vocab.word}">
                </div>
            ` : '';
            
            card.innerHTML = `
                ${imgHtml}
                <div class="flex-grow">
                    <div class="vocab-card-header flex justify-between items-start mb-2">
                        <div class="flex flex-wrap items-center gap-2">
                            <span class="vocab-word font-bold text-primary text-lg">🔹 ${vocab.word}</span>
                            <span class="vocab-ipa text-on-surface-variant text-sm font-medium">${vocab.pronunciation}</span>
                            <span class="vocab-type bg-secondary-container text-on-secondary-container px-2 py-0.5 rounded text-[10px] uppercase font-bold">${vocab.partOfSpeech}</span>
                        </div>
                        <button class="text-primary hover:text-primary-container transition-colors" onclick="speakWord('${vocab.word.replace(/'/g, "\\'")}')" title="Nghe phát âm">
                            <span class="material-symbols-outlined text-[20px]">volume_up</span>
                        </button>
                    </div>
                    <div class="vocab-meaning text-on-surface font-semibold mb-2">${vocab.meaning}</div>
                    <div class="vocab-example text-on-surface-variant text-sm italic"><strong>Example:</strong> "${vocab.example}"</div>
                </div>
            `;
            vocabGridEl.appendChild(card);
        });
    }

    // 4. Initialize Interactive Exercises (defined in vocab-exercises.js)
    if (typeof window.initExercises === 'function') {
        window.initExercises(unitData);
    } else {
        console.error('initExercises function not found in window!');
    }
}

// Speech Synthesis for English pronunciation
function speakWord(word) {
    if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel(); // Stop any active reading
        const utterance = new SpeechSynthesisUtterance(word);
        utterance.lang = 'en-US';
        
        // Pick an English voice
        const voices = window.speechSynthesis.getVoices();
        const enVoice = voices.find(v => v.lang.startsWith('en'));
        if (enVoice) {
            utterance.voice = enVoice;
        }
        
        window.speechSynthesis.speak(utterance);
    } else {
        alert('Your browser does not support audio pronunciation.');
    }
}

// Expose speakWord globally
if (typeof window !== 'undefined') {
    window.speakWord = speakWord;
}
