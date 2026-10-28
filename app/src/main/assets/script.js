// Функция вызова виброотклика
function haptic(type) {
    if (window.AndroidNative && window.AndroidNative.triggerHaptic) {
        window.AndroidNative.triggerHaptic(type);
    }
}

// Переключение между экранами
function openScreen(screenName) {
    haptic('click');
    const screens = {
        'home': 'screen-home',
        'camera': 'screen-camera',
        'analysis': 'screen-analysis',
        'verdict': 'screen-verdict',
        'timer': 'screen-timer',
        'profile': 'screen-profile'
    };

    const targetId = screens[screenName];
    if (!targetId) return;

    // Скрыть все экраны
    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));

    // Показать целевой
    const target = document.getElementById(targetId);
    if (target) {
        target.classList.add('active');
    }

    // Обновление активной вкладки в нижнем меню
    document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));
    if (screenName === 'camera' || screenName === 'home') {
        document.getElementById('nav-btn-scanner')?.classList.add('active');
    } else if (screenName === 'verdict') {
        document.getElementById('nav-btn-sorts')?.classList.add('active');
    } else if (screenName === 'timer') {
        document.getElementById('nav-btn-timer')?.classList.add('active');
    } else if (screenName === 'profile') {
        document.getElementById('nav-btn-history')?.classList.add('active');
    }
}

// Управление выпадающим списком кулинарных целей
function toggleRecipeDropdown(event) {
    if (event) event.stopPropagation();
    haptic('click');
    const dropdown = document.getElementById('recipe-dropdown');
    if (dropdown) {
        dropdown.classList.toggle('open');
    }
}

function selectRecipeOption(recipeName) {
    haptic('click');
    const textElem = document.getElementById('selected-recipe-text');
    if (textElem) {
        textElem.textContent = recipeName;
    }

    document.querySelectorAll('.recipe-option').forEach(opt => {
        if (opt.textContent.includes(recipeName)) {
            opt.classList.add('active');
        } else {
            opt.classList.remove('active');
        }
    });

    const dropdown = document.getElementById('recipe-dropdown');
    if (dropdown) {
        dropdown.classList.remove('open');
    }

    if (window.AndroidNative && window.AndroidNative.selectRecipe) {
        window.AndroidNative.selectRecipe(recipeName);
    }
}

// Переключение темы
function toggleTheme(isDark) {
    haptic('click');
    if (isDark) {
        document.body.classList.remove('light-theme');
    } else {
        document.body.classList.add('light-theme');
    }
    if (window.AndroidNative && window.AndroidNative.setDarkTheme) {
        window.AndroidNative.setDarkTheme(isDark);
    }
}

// Удаление сканирования из Room DB
function deleteScanHistory(id) {
    haptic('click');
    if (window.AndroidNative && window.AndroidNative.deleteScan) {
        window.AndroidNative.deleteScan(id);
    }
}

// Авторизация и выход
function handleAuthAction(isLoggedIn) {
    haptic('click');
    if (isLoggedIn) {
        if (confirm('Вы уверены, что хотите выйти из аккаунта?')) {
            if (window.AndroidNative && window.AndroidNative.logout) {
                window.AndroidNative.logout();
            }
        }
    } else {
        const email = prompt('Введите ваш Email для входа:');
        if (email && email.includes('@')) {
            const password = prompt('Введите пароль (минимум 6 символов):');
            if (password && password.length >= 6) {
                if (window.AndroidNative && window.AndroidNative.login) {
                    window.AndroidNative.login(email.trim(), password.trim());
                }
            } else if (password) {
                alert('Пароль должен содержать не менее 6 символов!');
            }
        } else if (email) {
            alert('Введите корректный адрес электронной почты!');
        }
    }
}

// Закрывать дропдаун при клике вне его области
document.addEventListener('click', function(e) {
    const wrap = document.querySelector('.recipe-selector-wrap');
    if (wrap && !wrap.contains(e.target)) {
        const dropdown = document.getElementById('recipe-dropdown');
        if (dropdown) {
            dropdown.classList.remove('open');
        }
    }
});

// Запуск анализа
function triggerAnalysis() {
    haptic('click');
    if (window.AndroidNative && window.AndroidNative.startAnalysis) {
        openScreen('analysis');
        window.AndroidNative.startAnalysis();
    } else {
        openScreen('analysis');
        let percent = 20;
        const fill = document.getElementById('analysis-fill');
        const pct = document.getElementById('analysis-pct');
        const interval = setInterval(() => {
            percent += 20;
            if (fill) fill.style.width = percent + '%';
            if (pct) pct.textContent = percent + '%';
            if (percent >= 100) {
                clearInterval(interval);
                setTimeout(() => openScreen('verdict'), 400);
            }
        }, 200);
    }
}

// Запуск съемки нативной камерой
function triggerCameraCapture() {
    haptic('click');
    if (window.AndroidNative && window.AndroidNative.openCamera) {
        window.AndroidNative.openCamera();
    } else {
        openScreen('camera');
    }
}

// Выбор файла из нативной галереи
function triggerGalleryPicker() {
    haptic('click');
    if (window.AndroidNative && window.AndroidNative.openGallery) {
        window.AndroidNative.openGallery();
    } else {
        triggerCameraCapture();
    }
}

// Редактирование имени профиля
function editProfileName() {
    haptic('click');
    const currentNameElem = document.querySelector('.user-display-name');
    const currentName = currentNameElem ? currentNameElem.textContent : 'BulbaLover';
    const newName = prompt('Введите новое имя профиля:', currentName);
    if (newName && newName.trim()) {
        if (window.AndroidNative && window.AndroidNative.updateProfileName) {
            window.AndroidNative.updateProfileName(newName.trim());
        } else if (currentNameElem) {
            currentNameElem.textContent = newName.trim();
        }
    }
}

// Управление таймером
function toggleTimer() {
    haptic('click');
    if (window.AndroidNative && window.AndroidNative.toggleTimer) {
        window.AndroidNative.toggleTimer();
    } else {
        const btn = document.getElementById('play-pause-btn');
        if (btn) btn.textContent = (btn.textContent === '▶') ? '❚❚' : '▶';
    }
}

function resetTimer() {
    haptic('click');
    if (window.AndroidNative && window.AndroidNative.resetTimer) {
        window.AndroidNative.resetTimer();
    } else {
        const display = document.getElementById('time-text');
        if (display) display.textContent = '04:00';
    }
}

// ДВУСТОРОННЯЯ СВЯЗЬ: Получение эмиттов State из Kotlin (StateFlow)
let lastScanProgress = 0;

window.onStateUpdate = function(rawJsonData) {
    try {
        const data = (typeof rawJsonData === 'string') ? JSON.parse(rawJsonData) : rawJsonData;
        if (!data || !data.type) return;

        if (data.type === 'home') {
            if (data.selectedRecipe) {
                const textElem = document.getElementById('selected-recipe-text');
                if (textElem) {
                    textElem.textContent = data.selectedRecipe;
                }
                document.querySelectorAll('.recipe-option').forEach(opt => {
                    if (opt.textContent.includes(data.selectedRecipe)) {
                        opt.classList.add('active');
                    } else {
                        opt.classList.remove('active');
                    }
                });
            }
        } else if (data.type === 'timer') {
            const display = document.getElementById('time-text');
            const btn = document.getElementById('play-pause-btn');
            const titleElem = document.querySelector('#screen-timer .timer-titles p');
            if (titleElem && data.title) {
                titleElem.textContent = data.title;
            }
            if (display && data.formattedTime) {
                display.textContent = data.formattedTime;
                if (data.remainingSeconds !== undefined && data.remainingSeconds <= 10 && data.remainingSeconds > 0) {
                    display.classList.add('heartbeat');
                } else {
                    display.classList.remove('heartbeat');
                }
            }
            if (btn && data.isRunning !== undefined) {
                btn.textContent = data.isRunning ? '❚❚' : '▶';
            }
        } else if (data.type === 'analysis') {
            const fill = document.getElementById('analysis-fill');
            const pct = document.getElementById('analysis-pct');
            if (fill) fill.style.width = data.progressPercentage + '%';
            if (pct) pct.textContent = data.progressPercentage + '%';

            if (data.progressPercentage > lastScanProgress && data.progressPercentage % 20 === 0) {
                haptic('scan_tick');
            }
            lastScanProgress = data.progressPercentage;

            if (data.isComplete && data.verdict) {
                haptic(data.verdict.score >= 7 ? 'success' : 'error');
                updateVerdictUi(data.verdict);
                setTimeout(() => openScreen('verdict'), 450);
            }
        } else if (data.type === 'profile') {
            document.querySelectorAll('.user-display-name').forEach(elem => {
                if (data.name) elem.textContent = data.name;
            });

            const emailElem = document.getElementById('user-profile-email');
            if (emailElem && data.email) {
                emailElem.textContent = data.email;
            }

            const authBtn = document.getElementById('auth-action-btn');
            if (authBtn) {
                authBtn.textContent = data.isLoggedIn ? 'Выйти из аккаунта' : 'Войти в аккаунт';
                authBtn.onclick = function() { handleAuthAction(data.isLoggedIn); };
            }

            if (data.isDarkTheme !== undefined) {
                if (data.isDarkTheme) {
                    document.body.classList.remove('light-theme');
                } else {
                    document.body.classList.add('light-theme');
                }
            }

            if (data.achievements && Array.isArray(data.achievements)) {
                renderAchievements(data.achievements);
            }

            if (data.scanHistory && Array.isArray(data.scanHistory)) {
                renderScanHistory(data.scanHistory);
            }
        }
    } catch (e) {
        console.error("Error in onStateUpdate:", e);
    }
};

// Отображение достижений (ачивок)
function renderAchievements(achievements) {
    const grid = document.querySelector('.achievements-grid');
    if (!grid) return;

    if (!achievements || achievements.length === 0) return;

    grid.innerHTML = achievements.map(ach => `
        <div class="achievement-card ${ach.isUnlocked ? 'unlocked' : 'locked'}" onclick="alert('${ach.title}: ${ach.description} (${ach.currentProgress}/${ach.targetProgress})')">
            <span>${ach.icon}</span>
            <div class="ach-badge">${ach.isUnlocked ? '✓' : '🔒'}</div>
        </div>
    `).join('');
}

// Отображение списка истории из Room DB
function renderScanHistory(historyList) {
    const container = document.getElementById('scan-history-list');
    if (!container) return;

    if (historyList.length === 0) {
        container.innerHTML = '<div class="empty-history">История сканирований пуста</div>';
        return;
    }

    container.innerHTML = historyList.map(item => `
        <div class="history-item-card">
            <div class="history-item-info">
                <strong>${item.varietyName || 'Клубень'} (${item.culinaryType || ''})</strong>
                <span>★ ${item.score}/10 — ${new Date(item.timestamp).toLocaleDateString()}</span>
            </div>
            <button class="history-delete-btn" onclick="deleteScanHistory('${item.id}')">🗑️</button>
        </div>
    `).join('');
}

// Обновление элементов карточки вердикта
function updateVerdictUi(verdict) {
    const titleElem = document.querySelector('#screen-verdict h3');
    if (titleElem && verdict.varietyName) {
        titleElem.textContent = '“' + verdict.varietyName + '”';
    }

    const typeElem = document.querySelector('#screen-verdict .type-caption strong');
    if (typeElem && verdict.culinaryType) {
        typeElem.textContent = verdict.culinaryType;
    }

    const scoreElem = document.querySelector('#screen-verdict .score-pill');
    if (scoreElem && verdict.score !== undefined) {
        scoreElem.textContent = '★ ' + verdict.score + '/10';
    }

    const textElem = document.querySelector('#screen-verdict .eval-text');
    if (textElem && verdict.verdictText) {
        textElem.textContent = verdict.verdictText;
    }

    const starchElem = document.querySelectorAll('#screen-verdict .tile-val')[0];
    if (starchElem && verdict.starchPercentage !== undefined) {
        starchElem.textContent = '~' + verdict.starchPercentage + '%';
    }

    const fleshElem = document.querySelectorAll('#screen-verdict .tile-val')[1];
    if (fleshElem && verdict.fleshColor) {
        fleshElem.textContent = verdict.fleshColor;
    }

    // Анимация штампа бейджа
    const badge = document.querySelector('#screen-verdict .badge-pill');
    if (badge) {
        badge.classList.remove('stamp-anim');
        void badge.offsetWidth; // Trigger reflow
        badge.classList.add('stamp-anim');
        badge.textContent = verdict.score >= 7 ? 'ПОДХОДИТ' : 'НЕ ПОДХОДИТ';
    }
}
