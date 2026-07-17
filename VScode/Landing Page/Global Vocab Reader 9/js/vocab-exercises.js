let currentUnitData = null;

// State of exercises
let matchingState = {
    selectedLeft: null,
    selectedRight: null,
    pairs: [], // current user matches: [{leftWord, rightWord, leftEl, rightEl}]
    checked: false
};

// Initialize Exercises for the loaded Unit
function initExercises(unitData) {
    currentUnitData = unitData;
    
    // Set up tabs navigation
    setupTabs();

    // Set up each exercise tab
    setupMatching();
    setupFillInTheBlank();
    setupMCQ();
}

// 1. --- TABS NAVIGATION ---
function setupTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabId = btn.getAttribute('data-tab');

            // Set active button
            tabButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            // Set active content
            tabContents.forEach(c => {
                c.classList.remove('active');
                if (c.id === `${tabId}-tab`) {
                    c.classList.add('active');
                }
            });

            // If switching to matching tab, redraw lines (since SVG coordinates need rendering)
            if (tabId === 'matching') {
                setTimeout(drawMatchingLines, 50);
            }
        });
    });
}

// 2. --- MATCHING GAME LOGIC ---
function setupMatching() {
    const leftContainer = document.getElementById('matching-left');
    const rightContainer = document.getElementById('matching-right');
    const svgEl = document.getElementById('matching-svg');
    const checkBtn = document.getElementById('check-matching');
    const resultEl = document.getElementById('matching-result');
    const userMatchesEl = document.getElementById('user-matches-list');

    if (!leftContainer || !rightContainer || !checkBtn) return;

    // Reset state
    matchingState = {
        selectedLeft: null,
        selectedRight: null,
        pairs: [],
        checked: false
    };
    if (svgEl) svgEl.innerHTML = '';
    if (resultEl) {
        resultEl.classList.remove('show', 'correct', 'incorrect');
        resultEl.textContent = '';
    }
    if (userMatchesEl) userMatchesEl.innerHTML = '';
    checkBtn.disabled = false;
    checkBtn.innerHTML = 'Check Answers';

    const vocabData = currentUnitData.exercises.matching.pairs;
    
    // Extract words and meanings
    const leftItems = vocabData.map(p => p.word);
    const rightItems = vocabData.map(p => p.meaning);

    // Shuffle both lists
    shuffleArray(leftItems);
    shuffleArray(rightItems);

    // Render columns
    leftContainer.innerHTML = leftItems.map(word => 
        `<div class="matching-item" data-word="${word}" id="match-left-${word}">${word}</div>`
    ).join('');

    rightContainer.innerHTML = rightItems.map(meaning => 
        `<div class="matching-item" data-meaning="${meaning}" id="match-right-${meaning.replace(/\s+/g, '_')}">${meaning}</div>`
    ).join('');

    // Attach click events
    leftContainer.querySelectorAll('.matching-item').forEach(item => {
        item.addEventListener('click', () => {
            if (matchingState.checked || item.classList.contains('matched')) return;

            // Highlight selection
            leftContainer.querySelectorAll('.matching-item').forEach(el => el.classList.remove('selected'));
            item.classList.add('selected');
            matchingState.selectedLeft = item;

            checkAndCreatePair();
        });
    });

    rightContainer.querySelectorAll('.matching-item').forEach(item => {
        item.addEventListener('click', () => {
            if (matchingState.checked || item.classList.contains('matched')) return;

            // Highlight selection
            rightContainer.querySelectorAll('.matching-item').forEach(el => el.classList.remove('selected'));
            item.classList.add('selected');
            matchingState.selectedRight = item;

            checkAndCreatePair();
        });
    });

    // Check if we can pair the selected elements
    function checkAndCreatePair() {
        if (matchingState.selectedLeft && matchingState.selectedRight) {
            const leftWord = matchingState.selectedLeft.getAttribute('data-word');
            const rightMeaning = matchingState.selectedRight.getAttribute('data-meaning');
            const leftEl = matchingState.selectedLeft;
            const rightEl = matchingState.selectedRight;

            // Add pair to state
            matchingState.pairs.push({
                word: leftWord,
                meaning: rightMeaning,
                leftEl: leftEl,
                rightEl: rightEl
            });

            // Mark elements as matched
            leftEl.classList.add('matched');
            rightEl.classList.add('matched');
            leftEl.classList.remove('selected');
            rightEl.classList.remove('selected');

            // Reset selection
            matchingState.selectedLeft = null;
            matchingState.selectedRight = null;

            // Draw connection lines
            drawMatchingLines();

            // Render pair in matches list
            renderUserMatchRow(leftWord, rightMeaning);
        }
    }

    // Render row of active match with a remove button
    function renderUserMatchRow(word, meaning) {
        if (!userMatchesEl) return;
        const row = document.createElement('div');
        row.className = 'match-pair-row';
        row.setAttribute('data-word', word);
        row.innerHTML = `
            <span><strong>${word}</strong> matched with <em>${meaning}</em></span>
            <button class="btn-remove-match" title="Remove Match">&times;</button>
        `;

        row.querySelector('.btn-remove-match').addEventListener('click', () => {
            if (matchingState.checked) return;
            // Remove match
            removePair(word);
        });

        userMatchesEl.appendChild(row);
    }

    // Remove a paired connection
    function removePair(word) {
        const index = matchingState.pairs.findIndex(p => p.word === word);
        if (index > -1) {
            const pair = matchingState.pairs[index];
            pair.leftEl.classList.remove('matched');
            pair.rightEl.classList.remove('matched');
            matchingState.pairs.splice(index, 1);
            
            // Remove row from UI
            const row = userMatchesEl.querySelector(`[data-word="${word}"]`);
            if (row) row.remove();

            // Redraw
            drawMatchingLines();
        }
    }

    // Check Answers
    checkBtn.onclick = () => {
        if (matchingState.checked) return;

        // Check if all pairs are matched
        if (matchingState.pairs.length < vocabData.length) {
            alert('Please complete all matches before checking!');
            return;
        }

        matchingState.checked = true;
        checkBtn.disabled = true;
        checkBtn.innerHTML = 'Checked ✅';

        let correctCount = 0;
        const pairsData = currentUnitData.exercises.matching.pairs;

        matchingState.pairs.forEach(pair => {
            // Find correct pairing
            const actualPair = pairsData.find(p => p.word === pair.word);
            const isCorrect = actualPair && actualPair.meaning === pair.meaning;

            const row = userMatchesEl.querySelector(`[data-word="${pair.word}"]`);

            if (isCorrect) {
                correctCount++;
                pair.leftEl.classList.add('correct');
                pair.rightEl.classList.add('correct');
                if (row) row.classList.add('correct');
            } else {
                pair.leftEl.classList.add('incorrect');
                pair.rightEl.classList.add('incorrect');
                if (row) {
                    row.classList.add('incorrect');
                    row.innerHTML += ` <span style="font-size: 12px;">(Correct: ${actualPair.meaning})</span>`;
                }
            }
        });

        // Redraw with colored feedback lines
        drawMatchingLines();

        // Show result box
        const total = vocabData.length;
        resultEl.classList.add('show');
        if (correctCount === total) {
            resultEl.className = 'exercise-result show correct';
            resultEl.innerHTML = `🎉 Awesome! All ${correctCount}/${total} words correct. You’ve learned the lesson really well!`;
        } else {
            resultEl.className = 'exercise-result show incorrect';
            resultEl.innerHTML = `✍️ Keep it up! ${correctCount}/${total} words correct. Take a look at the part you got wrong!`;
        }
        
        // Dispatch result event to potentially update unit done state
        if (correctCount === total) {
            markUnitCompleteIfAllDone();
        }
    };

    // Redraw lines when window resizes
    window.addEventListener('resize', drawMatchingLines);
}

// Calculate coords and draw lines on SVG
function drawMatchingLines() {
    const gameContainer = document.getElementById('matching-game-container');
    const svgEl = document.getElementById('matching-svg');
    if (!gameContainer || !svgEl) return;

    svgEl.innerHTML = ''; // clear

    const containerRect = gameContainer.getBoundingClientRect();
    const pairsData = currentUnitData ? currentUnitData.exercises.matching.pairs : [];

    matchingState.pairs.forEach(pair => {
        const leftRect = pair.leftEl.getBoundingClientRect();
        const rightRect = pair.rightEl.getBoundingClientRect();

        // Calculate coordinates relative to container
        const x1 = leftRect.right - containerRect.left;
        const y1 = (leftRect.top + leftRect.height / 2) - containerRect.top;

        const x2 = rightRect.left - containerRect.left;
        const y2 = (rightRect.top + rightRect.height / 2) - containerRect.top;

        // Set line color based on state
        let strokeColor = '#bc0b36'; // Default primary rose
        if (matchingState.checked) {
            const actualPair = pairsData.find(p => p.word === pair.word);
            const isCorrect = actualPair && actualPair.meaning === pair.meaning;
            strokeColor = isCorrect ? '#22C55E' : '#EF4444'; // Green or Red
        }

        // Create line element
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', x1);
        line.setAttribute('y1', y1);
        line.setAttribute('x2', x2);
        line.setAttribute('y2', y2);
        line.setAttribute('stroke', strokeColor);
        line.setAttribute('stroke-width', '3');
        line.setAttribute('stroke-linecap', 'round');
        
        // Add subtle animation for drawing lines
        line.style.strokeDasharray = '1000';
        line.style.strokeDashoffset = '1000';
        line.style.animation = 'dash 0.5s ease-out forwards';
        
        svgEl.appendChild(line);
    });
}

// 3. --- FILL IN THE BLANK LOGIC ---
function setupFillInTheBlank() {
    const fibContainer = document.getElementById('fib-questions-container');
    const checkBtn = document.getElementById('check-fib');
    const resultEl = document.getElementById('fib-result');

    if (!fibContainer || !checkBtn) return;

    // Reset UI
    fibContainer.innerHTML = '';
    if (resultEl) {
        resultEl.classList.remove('show', 'correct', 'incorrect');
        resultEl.textContent = '';
    }
    checkBtn.disabled = false;
    checkBtn.innerHTML = 'Check Answers';

    const questions = currentUnitData.exercises.fillInTheBlank;

    // Render questions
    questions.forEach((q, index) => {
        const qBlock = document.createElement('div');
        qBlock.className = 'fib-question';
        qBlock.innerHTML = `
            <div class="fib-sentence">${index + 1}. ${replaceBlankWithInput(q.sentence, index)}</div>
            <div class="fib-feedback" id="fib-feedback-${index}"></div>
        `;
        fibContainer.appendChild(qBlock);
    });

    // Helper to replace "_______" with HTML input tag
    function replaceBlankWithInput(sentence, index) {
        return sentence.replace(/_______/g, `<input type="text" class="fib-input" id="fib-input-${index}" placeholder="Type word..." autocomplete="off">`);
    }

    checkBtn.onclick = () => {
        let correctCount = 0;
        let answeredCount = 0;
        const total = questions.length;

        questions.forEach((q, index) => {
            const inputEl = document.getElementById(`fib-input-${index}`);
            const feedbackEl = document.getElementById(`fib-feedback-${index}`);

            if (!inputEl) return;
            const userAnswer = inputEl.value.trim().toLowerCase();
            const correctAnswer = q.answer.trim().toLowerCase();

            if (userAnswer) answeredCount++;

            inputEl.classList.remove('correct', 'incorrect');
            if (feedbackEl) {
                feedbackEl.className = 'fib-feedback';
                feedbackEl.textContent = '';
            }

            if (userAnswer === correctAnswer) {
                correctCount++;
                inputEl.classList.add('correct');
                if (feedbackEl) {
                    feedbackEl.classList.add('correct');
                    feedbackEl.innerHTML = '✓ Correct!';
                }
            } else {
                inputEl.classList.add('incorrect');
                if (feedbackEl) {
                    feedbackEl.classList.add('incorrect');
                    feedbackEl.innerHTML = `✗ Incorrect. Correct answer: <strong>${q.answer}</strong>`;
                }
            }
        });

        if (answeredCount < total) {
            if (!confirm('You have not filled in all blanks. Still want to submit?')) {
                return;
            }
        }

        // Disable inputs and button
        questions.forEach((q, index) => {
            const inputEl = document.getElementById(`fib-input-${index}`);
            if (inputEl) inputEl.disabled = true;
        });
        checkBtn.disabled = true;
        checkBtn.innerHTML = 'Checked ✅';

        // Show overall result
        resultEl.classList.add('show');
        if (correctCount === total) {
            resultEl.className = 'exercise-result show correct';
            resultEl.innerHTML = `🎉 Congratulations! Correct: ${correctCount}/${total} answers. You did an outstanding job!`;
        } else {
            resultEl.className = 'exercise-result show incorrect';
            resultEl.innerHTML = `✍️ Keep practicing! Correct: ${correctCount}/${total} answers. Review the correct answers above.`;
        }

        if (correctCount === total) {
            markUnitCompleteIfAllDone();
        }
    };
}

// 4. --- MULTIPLE CHOICE LOGIC (MCQ) ---
function setupMCQ() {
    const mcqContainer = document.getElementById('mcq-questions-container');
    const checkBtn = document.getElementById('check-mcq');
    const resultEl = document.getElementById('mcq-result');

    if (!mcqContainer || !checkBtn) return;

    // Reset UI
    mcqContainer.innerHTML = '';
    if (resultEl) {
        resultEl.classList.remove('show', 'correct', 'incorrect');
        resultEl.textContent = '';
    }
    checkBtn.disabled = false;
    checkBtn.innerHTML = 'Check Answers';

    const questions = currentUnitData.exercises.multipleChoice;

    // Render MCQ Questions
    questions.forEach((q, qIndex) => {
        const qBlock = document.createElement('div');
        qBlock.className = 'mcq-question-block';
        
        let optionsHtml = '';
        q.options.forEach((opt, optIndex) => {
            optionsHtml += `
                <label class="mcq-option-label cursor-pointer" id="label-mcq-${qIndex}-${optIndex}">
                    <input type="radio" name="mcq-${qIndex}" value="${optIndex}" class="w-5 h-5 text-primary border-[#e4bdbe] focus:ring-[#bc0b36]/20 cursor-pointer transition-all">
                    <span>${getOptionPrefix(optIndex)}. ${opt}</span>
                </label>
            `;
        });

        qBlock.innerHTML = `
            <div class="mcq-text">${qIndex + 1}. ${q.question}</div>
            <div class="mcq-options">
                ${optionsHtml}
            </div>
        `;
        mcqContainer.appendChild(qBlock);
    });

    // Option prefixes: A, B, C, D
    function getOptionPrefix(index) {
        return String.fromCharCode(65 + index); // 65 is ASCII for 'A'
    }

    // Set up highlight events for labels when option is chosen
    questions.forEach((q, qIndex) => {
        const labels = mcqContainer.querySelectorAll(`[id^="label-mcq-${qIndex}-"]`);
        labels.forEach(label => {
            const input = label.querySelector('input[type="radio"]');
            input.addEventListener('change', () => {
                labels.forEach(l => l.classList.remove('selected'));
                label.classList.add('selected');
            });
        });
    });

    checkBtn.onclick = () => {
        let correctCount = 0;
        let answeredCount = 0;
        const total = questions.length;

        questions.forEach((q, qIndex) => {
            const checkedInput = mcqContainer.querySelector(`input[name="mcq-${qIndex}"]:checked`);
            
            // Highlight labels
            const labels = mcqContainer.querySelectorAll(`[id^="label-mcq-${qIndex}-"]`);
            labels.forEach(l => l.classList.remove('correct', 'incorrect', 'selected'));

            if (checkedInput) {
                answeredCount++;
                const userIndex = parseInt(checkedInput.value, 10);
                const isCorrect = userIndex === q.correctIndex;

                const userLabel = document.getElementById(`label-mcq-${qIndex}-${userIndex}`);

                if (isCorrect) {
                    correctCount++;
                    userLabel.classList.add('correct');
                } else {
                    userLabel.classList.add('incorrect');
                    // Highlight the correct one
                    const correctLabel = document.getElementById(`label-mcq-${qIndex}-${q.correctIndex}`);
                    correctLabel.classList.add('correct');
                }
            } else {
                // If not answered, highlight the correct one
                const correctLabel = document.getElementById(`label-mcq-${qIndex}-${q.correctIndex}`);
                correctLabel.classList.add('correct');
            }
        });

        if (answeredCount < total) {
            if (!confirm('You have not answered all questions. Still want to submit?')) {
                return;
            }
        }

        // Disable all radio buttons
        mcqContainer.querySelectorAll('input[type="radio"]').forEach(r => r.disabled = true);
        checkBtn.disabled = true;
        checkBtn.innerHTML = 'Checked ✅';

        // Show overall result
        resultEl.classList.add('show');
        if (correctCount === total) {
            resultEl.className = 'exercise-result show correct';
            resultEl.innerHTML = `🎉 Great job! Correct: ${correctCount}/${total} questions. You got them all right!`;
        } else {
            resultEl.className = 'exercise-result show incorrect';
            resultEl.innerHTML = `✍️ Keep trying! Correct: ${correctCount}/${total} questions. Review the green highlights to study.`;
        }

        if (correctCount === total) {
            markUnitCompleteIfAllDone();
        }
    };
}

// 5. --- GENERAL HELPERS ---

// Fisher-Yates shuffle array algorithm
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
}

// Check if all exercises are successfully completed, then automatically mark the unit as learned
function markUnitCompleteIfAllDone() {
    // If unit is successfully completed in MCQ, Matching or Blank, we can mark it
    // Wait, the prompt says "Học sinh tick những Unit đã học ở trang Progress"
    // But we can also auto-complete or give feedback. Let's make sure the progress tracker function is available.
    if (currentUnitData && typeof window.saveProgress === 'function') {
        // Auto-save progress to local storage!
        window.saveProgress(currentUnitData.id, true);
        console.log(`Unit ${currentUnitData.id} automatically completed!`);
        
        // Automatically request feedback/experience rating if not rated yet
        if (typeof window.triggerAutoRatingModal === 'function') {
            window.triggerAutoRatingModal();
        }
    }
}

// Expose matching redraw and init functions
if (typeof window !== 'undefined') {
    window.initExercises = initExercises;
    window.drawMatchingLines = drawMatchingLines;
}
