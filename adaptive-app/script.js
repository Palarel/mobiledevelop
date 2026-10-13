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

    // Скрыть все экраны
    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));

    // Показать целевой
    const targetId = screens[screenName];
    if (targetId) {
        document.getElementById(targetId).classList.add('active');
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

// Запуск симуляции анализа
function triggerAnalysis() {
    openScreen('analysis');

    const fill = document.getElementById('analysis-fill');
    const pct = document.getElementById('analysis-pct');
    let percent = 20;

    const interval = setInterval(() => {
        percent += 20;
        if (percent >= 100) {
            percent = 100;
            clearInterval(interval);
            setTimeout(() => {
                openScreen('verdict');
            }, 450);
        }
        fill.style.width = percent + '%';
        pct.textContent = percent + '%';
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

function toggleTimer() {
    const btn = document.getElementById('play-pause-btn');
    if (timerRunning) {
        clearInterval(timerInterval);
        timerRunning = false;
        btn.textContent = '▶';
    } else {
        timerRunning = true;
        btn.textContent = '❚❚';
        timerInterval = setInterval(() => {
            if (timerSeconds > 0) {
                timerSeconds--;
                document.getElementById('time-text').textContent = formatTime(timerSeconds);
            } else {
                clearInterval(timerInterval);
                timerRunning = false;
                btn.textContent = '▶';
                alert('Время жарки вышло! Пора переворачивать клубни!');
            }
        }, 1000);
    }
}

function resetTimer() {
    clearInterval(timerInterval);
    timerRunning = false;
    timerSeconds = 240;
    document.getElementById('time-text').textContent = '04:00';
    document.getElementById('play-pause-btn').textContent = '▶';
}