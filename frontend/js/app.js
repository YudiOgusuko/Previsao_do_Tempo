const API_BASE = 'http://localhost:8080/api/clima';
const ENDPOINTS = {
  search:       (q) => `${API_BASE}/search?q=${encodeURIComponent(q)}`,
  coordination: (cidade, estado, pais) =>
    `${API_BASE}/coordination?cidade=${encodeURIComponent(cidade)}&estado=${encodeURIComponent(estado)}&pais=${encodeURIComponent(pais)}`,
  week:  (cidade, regiao, pais, dias) =>
    `${API_BASE}?cidade=${encodeURIComponent(cidade)}&regiao=${encodeURIComponent(regiao)}&pais=${encodeURIComponent(pais)}&dias=${dias}`,
  now:   (cidade, regiao, pais) =>
    `${API_BASE}/now?cidade=${encodeURIComponent(cidade)}&regiao=${encodeURIComponent(regiao)}&pais=${encodeURIComponent(pais)}`,
};


(function buildWatermark(){
  const el = document.getElementById('watermarkBg');
  for (let i = 0; i < 40; i++){
    const span = document.createElement('span');
    span.textContent = 'VICTOR YUDI OGUSUKO';
    el.appendChild(span);
  }
})();


function enableDragScroll(listEl){
  let isDown = false, startY = 0, startScroll = 0;
  listEl.addEventListener('mousedown', (e) => {
    isDown = true;
    listEl.classList.add('is-dragging');
    startY = e.pageY;
    startScroll = listEl.scrollTop;
  });
  window.addEventListener('mouseup', () => { isDown = false; listEl.classList.remove('is-dragging'); });
  listEl.addEventListener('mouseleave', () => { isDown = false; listEl.classList.remove('is-dragging'); });
  listEl.addEventListener('mousemove', (e) => {
    if (!isDown) return;
    e.preventDefault();
    listEl.scrollTop = startScroll - (e.pageY - startY);
  });
}

function closeList(listEl, inputEl){
  listEl.hidden = true;
  inputEl.setAttribute('aria-expanded', 'false');
}
function openList(listEl, inputEl){
  listEl.hidden = false;
  inputEl.setAttribute('aria-expanded', 'true');
}

document.addEventListener('click', (e) => {
  if (!e.target.closest('#cityCombobox')) closeList(cityList, cityInput);
  if (!e.target.closest('#daysCombobox')) closeList(daysList, daysInput);
});


const cityInput = document.getElementById('cityInput');
const cityList  = document.getElementById('cityList');
enableDragScroll(cityList);

let citySuggestions = [];
let cityActiveIndex = -1;
let selectedCity = null; 
let searchTimer = null;

function normalize(v){ return (v || '').toString(); }

function renderCityList(items){
  cityList.innerHTML = '';
  cityActiveIndex = -1;
  if (!items.length){
    const li = document.createElement('li');
    li.className = 'is-empty';
    li.textContent = 'Nenhuma cidade encontrada.';
    cityList.appendChild(li);
    openList(cityList, cityInput);
    return;
  }
  items.forEach((item, idx) => {
    const li = document.createElement('li');
    li.setAttribute('role', 'option');
    li.innerHTML = `<span class="city-name">${normalize(item.cidade)}</span> <span class="city-meta">(${normalize(item.estado)}, ${normalize(item.pais)})</span>`;
    li.addEventListener('click', () => selectCity(item));
    li.addEventListener('mousemove', () => setActiveCity(idx));
    cityList.appendChild(li);
  });
  openList(cityList, cityInput);
}

function setActiveCity(idx){
  const lis = cityList.querySelectorAll('li[role="option"]');
  lis.forEach((el) => el.classList.remove('is-active'));
  if (lis[idx]){
    lis[idx].classList.add('is-active');
    lis[idx].scrollIntoView({ block: 'nearest' });
  }
  cityActiveIndex = idx;
}

async function selectCity(item){
  try{
    setStatus('Confirmando localização...');
    const res = await fetch(ENDPOINTS.coordination(item.cidade, item.estado, item.pais));
    if (!res.ok) throw new Error('coordination-failed');
    const data = await res.json();
    const coord = Array.isArray(data) ? data[0] : data;
    if (!coord) throw new Error('coordination-empty');
    selectedCity = {
      cidade: item.cidade,
      estado: item.estado,
      pais: item.pais,
      latitude:  coord.latitude  ?? coord.lat,
      longitude: coord.longitude ?? coord.lon ?? coord.lng,
    };
    cityInput.value = `${item.cidade} (${item.estado}, ${item.pais})`;
    clearError();
    setStatus('');
  }catch(err){
    selectedCity = null;
    showError('Não foi possível confirmar as coordenadas dessa cidade. Verifique o endpoint /coordination do backend.');
  }
  closeList(cityList, cityInput);
}

cityInput.addEventListener('input', () => {
  selectedCity = null;
  const q = cityInput.value.trim();
  clearTimeout(searchTimer);
  if (q.length === 0){ closeList(cityList, cityInput); return; }
  searchTimer = setTimeout(async () => {
    try{
      const res = await fetch(ENDPOINTS.search(q));
      if (!res.ok) throw new Error('search-failed');
      const data = await res.json();
      citySuggestions = (Array.isArray(data) ? data : []).map((it) => ({
        cidade: it.cidade ?? it.name ?? '',
        estado: it.estado ?? it.regiao ?? it.region ?? '',
        pais:   it.pais ?? it.country ?? '',
      })).sort((a, b) =>
        a.cidade.localeCompare(b.cidade) || a.estado.localeCompare(b.estado) || a.pais.localeCompare(b.pais)
      );
      renderCityList(citySuggestions);
    }catch(err){
      citySuggestions = [];
      renderCityList([]);
    }
  }, 300);
});

cityInput.addEventListener('keydown', (e) => {
  const lis = cityList.querySelectorAll('li[role="option"]');
  if (cityList.hidden || !lis.length) return;
  if (e.key === 'ArrowDown'){ e.preventDefault(); setActiveCity(Math.min(cityActiveIndex + 1, lis.length - 1)); }
  else if (e.key === 'ArrowUp'){ e.preventDefault(); setActiveCity(Math.max(cityActiveIndex - 1, 0)); }
  else if (e.key === 'Enter'){ e.preventDefault(); if (cityActiveIndex >= 0) selectCity(citySuggestions[cityActiveIndex]); }
  else if (e.key === 'Escape'){ closeList(cityList, cityInput); }
});


const daysInput = document.getElementById('daysInput');
const daysList  = document.getElementById('daysList');
enableDragScroll(daysList);
let selectedDays = null;
let daysActiveIndex = -1;

(function buildDaysList(){
  for (let d = 1; d <= 14; d++){
    const li = document.createElement('li');
    li.setAttribute('role', 'option');
    li.textContent = d === 1 ? '1 dia' : `${d} dias`;
    li.addEventListener('click', () => selectDays(d, li.textContent));
    li.addEventListener('mousemove', () => setActiveDays(Array.from(daysList.children).indexOf(li)));
    daysList.appendChild(li);
  }
})();

function setActiveDays(idx){
  Array.from(daysList.children).forEach((el) => el.classList.remove('is-active'));
  if (daysList.children[idx]){
    daysList.children[idx].classList.add('is-active');
    daysList.children[idx].scrollIntoView({ block: 'nearest' });
  }
  daysActiveIndex = idx;
}

function selectDays(value, label){
  selectedDays = value;
  daysInput.value = label;
  closeList(daysList, daysInput);
  clearError();
}

daysInput.addEventListener('click', () => {
  if (daysList.hidden) openList(daysList, daysInput); else closeList(daysList, daysInput);
});
daysInput.addEventListener('keydown', (e) => {
  if (e.key === 'Enter' || e.key === ' '){ e.preventDefault(); openList(daysList, daysInput); return; }
  if (daysList.hidden) return;
  if (e.key === 'ArrowDown'){ e.preventDefault(); setActiveDays(Math.min(daysActiveIndex + 1, 13)); }
  else if (e.key === 'ArrowUp'){ e.preventDefault(); setActiveDays(Math.max(daysActiveIndex - 1, 0)); }
  else if (e.key === 'Enter'){ e.preventDefault(); if (daysActiveIndex >= 0) daysList.children[daysActiveIndex].click(); }
  else if (e.key === 'Escape'){ closeList(daysList, daysInput); }
});


const statusLine = document.getElementById('statusLine');
const errorBox = document.getElementById('errorBox');
const errorText = document.getElementById('errorText');
const demoBanner = document.getElementById('demoBanner');

function setStatus(msg){
  statusLine.hidden = !msg;
  statusLine.textContent = msg;
}
function showError(msg){
  errorBox.hidden = false;
  errorText.textContent = msg;
  setStatus('');
}
function clearError(){
  errorBox.hidden = true;
  demoBanner.hidden = true;
}


const resultsSection = document.getElementById('results');
const locationLine = document.getElementById('locationLine');
const forecastGrid = document.getElementById('forecastGrid');

function renderLocation(cidade, regiao, pais){
  locationLine.innerHTML = `<div class="city">${cidade}</div><div class="region">${regiao}, ${pais}</div>`;
}

function renderWeek(list){
  forecastGrid.className = 'forecast-grid';
  forecastGrid.innerHTML = '';
  list.forEach((day) => {
    const card = document.createElement('div');
    card.className = 'day-card';
    card.innerHTML = `
      <span class="weekday">${day.diaDaSemana ?? ''}</span>
      <span class="date">${formatDate(day.data)}</span>
      ${pickIcon(day.descricao)}
      <p class="desc">${day.descricao ?? ''}</p>
      <div class="temps"><span class="min">${round(day.temperaturaMinima)}°</span><span class="max">${round(day.temperaturaMaxima)}°</span></div>
      <span class="humidity">Umidade: ${day.umidade ?? '--'}%</span>
    `;
    forecastGrid.appendChild(card);
  });
  renderLocation(list[0]?.cidade ?? '', list[0]?.regiao ?? '', list[0]?.pais ?? '');
  resultsSection.hidden = false;
}

function renderNow(now){
  forecastGrid.className = 'forecast-grid single';
  forecastGrid.innerHTML = `
    <div class="day-card current-card">
      <span class="weekday">${now.diaDaSemana ?? ''}</span>
      <span class="date">${formatDate(now.data)}</span>
      ${pickIcon(now.descricao)}
      <div class="temp-now">${round(now.temperaturaAtual)}°</div>
      <p class="desc">${now.descricao ?? ''}</p>
      <span class="humidity">Umidade: ${now.umidade ?? '--'}%</span>
    </div>
  `;
  renderLocation(now.cidade ?? '', now.regiao ?? '', now.pais ?? '');
  resultsSection.hidden = false;
}

function round(n){ return (typeof n === 'number') ? Math.round(n) : (n ?? '--'); }
function formatDate(value){
  if (!value) return '';
  const d = new Date(value);
  if (isNaN(d)) return String(value).slice(0, 10);
  return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
}

const btnBuscar = document.getElementById('btnBuscar');
const btnAtual  = document.getElementById('btnAtual');
const btnDemo   = document.getElementById('btnDemo');


async function withLoading(btn, fn){
  const original = btn.textContent;
  btn.disabled = true;
  btnBuscar.disabled = true; btnAtual.disabled = true;
  btn.textContent = 'Buscando...';
  try{ await fn(); }
  finally{ btn.textContent = original; btnBuscar.disabled = false; btnAtual.disabled = false; }
}

btnBuscar.addEventListener('click', () => withLoading(btnBuscar, async () => {
  clearError();
  if (!selectedCity){ showError('Selecione uma cidade na lista de sugestões.'); return; }
  if (!selectedDays){ showError('Selecione a quantidade de dias (1 a 14).'); return; }
  try{
    const res = await fetch(ENDPOINTS.week(selectedCity.cidade, selectedCity.estado, selectedCity.pais, selectedDays));
    if (!res.ok) throw new Error('week-failed');
    const data = await res.json();
    renderWeek(data);
  }catch(err){
    showError('Não foi possível buscar a previsão. Verifique se o backend está rodando e se o CORS está habilitado.');
  }
}));

btnAtual.addEventListener('click', () => withLoading(btnAtual, async () => {
  clearError();
  if (!selectedCity){ showError('Selecione uma cidade na lista de sugestões.'); return; }
  try{
    const res = await fetch(ENDPOINTS.now(selectedCity.cidade, selectedCity.estado, selectedCity.pais));
    if (!res.ok) throw new Error('now-failed');
    const data = await res.json();
    renderNow(data);
  }catch(err){
    showError('Não foi possível buscar o clima atual. Verifique se o backend está rodando e se o CORS está habilitado.');
  }
}));


btnDemo.addEventListener('click', () => {
  clearError();
  demoBanner.hidden = false;
  const demoWeek = [
    { cidade:'São Paulo', regiao:'São Paulo', pais:'Brasil', diaDaSemana:'Terça-feira', data:'2026-09-22', descricao:'Sol com muitas nuvens. Pancadas de chuva à tarde e à noite.', temperaturaMinima:16, temperaturaMaxima:22, umidade:64 },
    { cidade:'São Paulo', regiao:'São Paulo', pais:'Brasil', diaDaSemana:'Quarta-feira', data:'2026-09-23', descricao:'Chuvoso durante o dia e à noite.', temperaturaMinima:12, temperaturaMaxima:15, umidade:81 },
    { cidade:'São Paulo', regiao:'São Paulo', pais:'Brasil', diaDaSemana:'Quinta-feira', data:'2026-09-24', descricao:'Sol com muitas nuvens. Pancadas de chuva à tarde e à noite.', temperaturaMinima:12, temperaturaMaxima:18, umidade:70 },
    { cidade:'São Paulo', regiao:'São Paulo', pais:'Brasil', diaDaSemana:'Sexta-feira', data:'2026-09-25', descricao:'Sol com muitas nuvens durante o dia e períodos de céu nublado.', temperaturaMinima:15, temperaturaMaxima:25, umidade:58 },
    { cidade:'São Paulo', regiao:'São Paulo', pais:'Brasil', diaDaSemana:'Sábado', data:'2026-09-26', descricao:'Dia de sol com névoa fraca ao amanhecer.', temperaturaMinima:18, temperaturaMaxima:27, umidade:50 },
    { cidade:'São Paulo', regiao:'São Paulo', pais:'Brasil', diaDaSemana:'Domingo', data:'2026-09-27', descricao:'Sol o dia todo sem nuvens no céu.', temperaturaMinima:18, temperaturaMaxima:28, umidade:47 },
    { cidade:'São Paulo', regiao:'São Paulo', pais:'Brasil', diaDaSemana:'Segunda-feira', data:'2026-09-28', descricao:'Trovoadas isoladas à tarde.', temperaturaMinima:19, temperaturaMaxima:26, umidade:66 },
  ];
  renderWeek(demoWeek);
});
