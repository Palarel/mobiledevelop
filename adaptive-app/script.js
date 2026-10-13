// Переключение между экранами
function openScreen(screenName) {
    const screens = {
        'home': 'screen-home',
        'camera': 'screen-camera',
        'analysis': 'screen-analysis',
        'verdict': 'screen-verdict',
        'timer': 'screen-timer',
        'profile': 'screen-profile'
    };

    const targetId = screens[screenName];
    if (!targetId) {
        return;
    }

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

let analysisIntervalId = null;
let analysisTimeoutId = null;

// Запуск симуляции анализа
function triggerAnalysis() {
    if (analysisIntervalId) {
        clearInterval(analysisIntervalId);
    }
    if (analysisTimeoutId) {
        clearTimeout(analysisTimeoutId);
    }

    openScreen('analysis');

    const fill = document.getElementById('analysis-fill');
    const pct = document.getElementById('analysis-pct');
    let percent = 20;

    fill && (fill.style.width = percent + '%');
    pct && (pct.textContent = percent + '%');

    analysisIntervalId = setInterval(() => {
        percent += 20;
        if (percent >= 100) {
            percent = 100;
            clearInterval(analysisIntervalId);
            analysisIntervalId = null;
            analysisTimeoutId = setTimeout(() => {
                openScreen('verdict');
            }, 450);
        }
        if (fill) {
            fill.style.width = percent + '%';
        }
        if (pct) {
            pct.textContent = percent + '%';
        }
    }, 150);
}

// Логика таймера
let timerSeconds = 240; // 4:00
let timerInterval = null;
let timerRunning = false;

function formatTime(sec) {
    const m = Math.floor(sec / 60).toString().padStart(2, '0');
    const s = (sec % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
}

function syncTimerUi() {
    const display = document.getElementById('time-text');
    const btn = document.getElementById('play-pause-btn');
    if (display) {
        display.textContent = formatTime(timerSeconds);
    }
    if (btn) {
        btn.textContent = timerRunning ? '❚❚' : '▶';
    }
}

function toggleTimer() {
    const btn = document.getElementById('play-pause-btn');
    if (!btn) {
        return;
    }

    if (timerRunning) {
        clearInterval(timerInterval);
        timerInterval = null;
        timerRunning = false;
        btn.textContent = '▶';
        return;
    }

    if (timerSeconds <= 0) {
        timerSeconds = 240;
    }

    timerRunning = true;
    btn.textContent = '❚❚';
    timerInterval = setInterval(() => {
        if (timerSeconds > 0) {
            timerSeconds--;
            const display = document.getElementById('time-text');
            if (display) {
                display.textContent = formatTime(timerSeconds);
            }
        } else {
            clearInterval(timerInterval);
            timerInterval = null;
            timerRunning = false;
            btn.textContent = '▶';
            alert('Время жарки вышло! Пора переворачивать клубни!');
        }
    }, 1000);
}

function resetTimer() {
    clearInterval(timerInterval);
    timerInterval = null;
    timerRunning = false;
    timerSeconds = 240;
    syncTimerUi();
}