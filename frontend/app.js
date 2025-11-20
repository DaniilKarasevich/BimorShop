// app.js — Обновленный UI/UX

const API_ROOT = '/api';

let currentAccount = JSON.parse(localStorage.getItem('currentAccount') || 'null');
const dom = {};
document.addEventListener('DOMContentLoaded', init);

function init(){
    cacheDom();
    bindEvents();
    renderAuthState();
    showHome();
    loadProducts();
}

function cacheDom(){
    dom.productsGrid = document.getElementById('products-grid');
    dom.modal = document.getElementById('modal');
    dom.modalBody = document.getElementById('modal-body');
    dom.modalClose = document.getElementById('modal-close');
    dom.authModal = document.getElementById('auth-modal');
    dom.authClose = document.getElementById('auth-close');
    dom.profileView = document.getElementById('profile-view');
    dom.catalogView = document.getElementById('catalog-view');

    // Navigation
    dom.btnHome = document.getElementById('btn-home');
    dom.btnProfile = document.getElementById('btn-profile');
    dom.btnLogin = document.getElementById('btn-login');
    dom.btnLogout = document.getElementById('btn-logout');

    // ПОИСК (изменено здесь)
    dom.searchInput = document.getElementById('search-input');
    dom.btnFilter = document.getElementById('btn-filter');
    dom.btnClearFilter = document.getElementById('btn-clear-filter');

    // Profile fields
    dom.accountInfo = document.getElementById('account-info');
    dom.favoritesList = document.getElementById('favorites-list');
    dom.myProducts = document.getElementById('my-products');
    dom.productForm = document.getElementById('product-form');
    dom.productId = document.getElementById('product-id');
    dom.productName = document.getElementById('product-name');
    dom.productPrice = document.getElementById('product-price');
    dom.productCategory = document.getElementById('product-category');
    dom.productImage = document.getElementById('product-image');
}

function bindEvents(){
    dom.modalClose.addEventListener('click', ()=>hide(dom.modal));
    dom.authClose.addEventListener('click', ()=>hide(dom.authModal));
    dom.btnProfile.addEventListener('click', showProfile);
    dom.btnHome.addEventListener('click', showHome);
    dom.btnLogin.addEventListener('click', ()=>showAuth());
    dom.btnLogout.addEventListener('click', doLogout);

    // ЛОГИКА ПОИСКА (изменено здесь)
    dom.btnFilter.addEventListener('click', loadProducts);

    // Поиск по нажатию Enter в поле ввода
    dom.searchInput.addEventListener('keyup', (e) => {
        if(e.key === 'Enter') loadProducts();
    });

    // Сброс поиска
    dom.btnClearFilter.addEventListener('click', ()=>{
        dom.searchInput.value = '';
        loadProducts();
    });

    // Auth tabs
    document.getElementById('show-login').addEventListener('click', ()=>toggleAuthTab('login'));
    document.getElementById('show-register').addEventListener('click', ()=>toggleAuthTab('register'));
    document.getElementById('btn-do-login').addEventListener('click', doLogin);
    document.getElementById('btn-do-register').addEventListener('click', doRegister);

    // Product form
    dom.productForm.addEventListener('submit', handleProductForm);
    document.getElementById('btn-clear-product').addEventListener('click', clearProductForm);

    // Закрытие модалок по клику на фон
    dom.modal.addEventListener('click', (e) => { if(e.target === dom.modal) hide(dom.modal); });
    dom.authModal.addEventListener('click', (e) => { if(e.target === dom.authModal) hide(dom.authModal); });
}

function renderAuthState(){
    if(currentAccount){
        document.getElementById('btn-login').classList.add('hidden');
        document.getElementById('btn-logout').classList.remove('hidden');
        dom.btnProfile.classList.remove('hidden');
    } else {
        document.getElementById('btn-login').classList.remove('hidden');
        document.getElementById('btn-logout').classList.add('hidden');
        // dom.btnProfile.classList.add('hidden'); // Можно скрыть кнопку профиля, если не залогинен
    }
}

// ---------- UX & NOTIFICATIONS ----------
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    let icon = type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle';
    toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${message}</span>`;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// ---------- VIEWS ----------
function showHome(){
    dom.catalogView.classList.remove('hidden');
    dom.profileView.classList.add('hidden');
    dom.btnHome.classList.add('active');
    dom.btnProfile.classList.remove('active');
}

function showProfile(){
    if(!currentAccount){ showAuth(); return; }
    dom.catalogView.classList.add('hidden');
    dom.profileView.classList.remove('hidden');
    dom.btnHome.classList.remove('active');
    dom.btnProfile.classList.add('active');
    loadProfile();
}

// ---------- AUTH ----------
function showAuth(){
    dom.authModal.classList.remove('hidden');
    toggleAuthTab('login');
}
function toggleAuthTab(tab){
    document.getElementById('show-login').classList.toggle('active', tab==='login');
    document.getElementById('show-register').classList.toggle('active', tab==='register');
    document.getElementById('login-form').classList.toggle('hidden', tab!=='login');
    document.getElementById('register-form').classList.toggle('hidden', tab!=='register');
}

async function doLogin(e){
    e?.preventDefault?.();
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value.trim();
    if(!email || !password){ showToast('Введите email и пароль', 'error'); return; }

    try{
        const res = await fetch(`${API_ROOT}/accounts/login`, {
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body: JSON.stringify({ email, password })
        });
        if(!res.ok) throw new Error('Неверные данные');

        const account = await res.json();
        currentAccount = account;
        localStorage.setItem('currentAccount', JSON.stringify(account));
        renderAuthState();
        hide(dom.authModal);
        showToast(`Добро пожаловать, ${account.firstName}!`, 'success');
        showProfile();
    }catch(err){
        showToast(err.message, 'error');
    }
}

async function doRegister(e){
    e?.preventDefault?.();
    // Сбор данных (код без изменений логики)
    const nickname = document.getElementById('reg-nickname').value.trim();
    const firstName = document.getElementById('reg-firstName').value.trim();
    const lastName = document.getElementById('reg-lastName').value.trim();
    const phone = document.getElementById('reg-phone').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const pass2 = document.getElementById('reg-password2').value;

    if(!nickname||!firstName||!lastName||!phone||!email||!password){ showToast('Заполните все поля', 'error'); return; }
    if(password !== pass2){ showToast('Пароли не совпадают', 'error'); return; }

    const payload = { nickname, firstName, lastName, phone, email, password };
    try{
        const res = await fetch(`${API_ROOT}/accounts/register`, {
            method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload)
        });
        if(!res.ok) throw new Error((await res.text()) || 'Ошибка регистрации');

        const account = await res.json();
        currentAccount = account;
        localStorage.setItem('currentAccount', JSON.stringify(account));
        renderAuthState();
        hide(dom.authModal);
        showToast('Регистрация успешна!', 'success');
        showProfile();
    }catch(err){
        showToast(err.message, 'error');
    }
}

function doLogout(){
    currentAccount = null;
    localStorage.removeItem('currentAccount');
    renderAuthState();
    showHome();
    showToast('Вы вышли из системы');
}

// ---------- PRODUCTS ----------
async function loadProducts(){
    dom.productsGrid.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:20px;color:#777"><i class="fa-solid fa-circle-notch fa-spin"></i> Загрузка...</div>';

    // 1. Берем текст поиска и приводим к нижнему регистру
    const searchTerm = dom.searchInput.value.trim().toLowerCase();

    try{
        // 2. Загружаем ВСЕ товары (без параметров URL, фильтруем на фронте)
        const res = await fetch(`${API_ROOT}/products`);
        if(!res.ok) throw new Error('Ошибка загрузки');
        let products = await res.json();

        // 3. Если есть поисковый запрос, фильтруем массив
        if(searchTerm) {
            products = products.filter(p =>
                p.name.toLowerCase().includes(searchTerm) ||
                p.category.toLowerCase().includes(searchTerm) // Можно искать и по категории тоже
            );
        }

        // Загрузка избранного для сопоставления
        let favSet = new Set();
        if(currentAccount){
            try{
                const r2 = await fetch(`${API_ROOT}/orders/favorite/${currentAccount.id}`);
                if(r2.ok) (await r2.json()).forEach(p=> favSet.add(Number(p.id)));
            }catch(e){ console.warn(e); }
        }
        products.forEach(p => { p._isFav = favSet.has(Number(p.id)); });

        // Рендерим (отображаем)
        renderProducts(products);

    }catch(err){
        dom.productsGrid.innerHTML = `<div style="grid-column:1/-1;text-align:center;color:red">${err.message}</div>`;
    }
}

function renderProducts(products){
    dom.productsGrid.innerHTML = '';
    if(!products || products.length===0){
        dom.productsGrid.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:40px;color:#999">Товары не найдены</div>'; return;
    }
    products.forEach(p => {
        const card = document.createElement('div');
        card.className = 'card product-card';

        const imgUrl = p.imageUrl || 'https://via.placeholder.com/400x300?text=No+Image';

        card.innerHTML = `
            <div class="card-img-wrapper">
                <img src="${imgUrl}" alt="${escapeHtml(p.name)}">
            </div>
            <div class="card-body">
                <div class="card-title">${escapeHtml(p.name)}</div>
                <div style="color:#6b7280;font-size:0.9em;margin-bottom:8px">${escapeHtml(p.category)}</div>
                <div class="card-price">${escapeHtml(p.price)} BYN</div>
                
                <div class="card-footer">
                    <button class="btn-primary" style="flex:1;padding:8px">Подробнее</button>
                    <button class="btn-fav ${p._isFav ? 'active' : 'not-active'}" title="${p._isFav ? 'Убрать' : 'В избранное'}">
                        <i class="fa-solid fa-heart"></i>
                    </button>
                </div>
            </div>
        `;

        // Events
        card.querySelector('.btn-primary').addEventListener('click', () => showProductModal(p));
        card.querySelector('.btn-fav').addEventListener('click', (e) => {
            e.stopPropagation();
            toggleFavoriteProduct(p);
        });

        dom.productsGrid.appendChild(card);
    });
}

function showProductModal(product){
    const imgUrl = product.imageUrl || 'https://via.placeholder.com/600x400?text=No+Image';
    const sellerName = product.account ? `${product.account.firstName} ${product.account.lastName}` : 'Неизвестен';

    // Проверяем, есть ли данные аккаунта, чтобы достать телефон и email
    const sellerEmail = product.account ? escapeHtml(product.account.email) : '-';
    const sellerPhone = product.account ? escapeHtml(product.account.phone) : '-';

    const html = `
    <div class="product-detail">
        <div class="detail-img">
            <img src="${imgUrl}" alt="${escapeHtml(product.name)}" />
        </div>
        <div class="detail-info">
            <h2 style="margin-top:0">${escapeHtml(product.name)}</h2>
            <div style="font-size:1.5rem;color:var(--primary);font-weight:bold;margin:10px 0">${escapeHtml(product.price)} BYN</div>
            <div style="color:#666;margin-bottom:20px">Категория: <span style="background:#eee;padding:2px 8px;border-radius:4px">${escapeHtml(product.category)}</span></div>
            
            <div class="seller-badge">
                <div style="font-weight:600;margin-bottom:4px"><i class="fa-solid fa-store"></i> Продавец</div>
                <div>${escapeHtml(sellerName)}</div>
                
                <div style="font-size:0.9em;color:#555;margin-top:4px">
                   <i class="fa-solid fa-envelope"></i> ${sellerEmail}
                </div>

                <div style="font-size:0.9em;color:#555;margin-top:4px">
                   <i class="fa-solid fa-phone"></i> ${sellerPhone}
                </div>
            </div>

            <button id="modal-fav-btn" class="btn-primary full-width" style="margin-top:20px">
                ${product._isFav ? '<i class="fa-solid fa-heart-broken"></i> Убрать из избранного' : '<i class="fa-solid fa-heart"></i> Добавить в избранное'}
            </button>
        </div>
    </div>
    `;

    dom.modalBody.innerHTML = html;
    show(dom.modal);

    document.getElementById('modal-fav-btn').addEventListener('click', async () => {
        hide(dom.modal);
        await toggleFavoriteProduct(product);
    });
}

// ---------- FAVORITE ACTIONS ----------
async function toggleFavoriteProduct(product){
    if(!currentAccount){ showAuth(); return; }

    const action = product._isFav ? 'DELETE' : 'POST';
    try {
        const res = await fetch(`${API_ROOT}/orders/favorite?accountId=${currentAccount.id}&productId=${product.id}`, { method: action });
        if(!res.ok) throw new Error('Ошибка сервера');

        product._isFav = !product._isFav;

        showToast(product._isFav ? 'Добавлено в избранное' : 'Удалено из избранного', 'success');

        // Обновляем UI везде
        loadProducts();
        if(!dom.profileView.classList.contains('hidden')) loadProfile();

    } catch(err) {
        showToast('Не удалось изменить избранное', 'error');
    }
}

// ---------- PROFILE ----------
async function loadProfile(){
    dom.accountInfo.innerHTML = `
        <h3>${escapeHtml(currentAccount.firstName)} ${escapeHtml(currentAccount.lastName)}</h3>
        <p>@${escapeHtml(currentAccount.nickname)}</p>
        <p><i class="fa-solid fa-envelope"></i> ${escapeHtml(currentAccount.email)}</p>
        <p><i class="fa-solid fa-phone"></i> ${escapeHtml(currentAccount.phone)}</p>
    `;

    // Favorites
    try{
        const res = await fetch(`${API_ROOT}/orders/favorite/${currentAccount.id}`);
        if(res.ok){
            const favs = await res.json();
            renderFavorites(favs);
        }
    }catch(e){ console.error(e); }

    // My Products
    try{
        const res = await fetch(`${API_ROOT}/products`);
        if(res.ok){
            const all = await res.json();
            const mine = all.filter(p => p.account && Number(p.account.id) === Number(currentAccount.id));
            renderMyProducts(mine);
        }
    }catch(e){ console.error(e); }
}

function renderFavorites(products){
    const list = dom.favoritesList;
    list.innerHTML = '';
    if(!products || !products.length){ list.innerHTML = '<div style="color:#999">Список пуст</div>'; return; }

    // Используем ту же карточку, но упрощенную
    products.forEach(p => {
        const div = document.createElement('div');
        div.className = 'card product-card';
        // ИЗМЕНЕНИЕ НИЖЕ: Цена с BYN
        div.innerHTML = `
            <div class="card-img-wrapper" style="height:140px">
                <img src="${p.imageUrl || 'https://via.placeholder.com/300'}" />
            </div>
            <div class="card-body" style="padding:10px">
                <div class="card-title" style="font-size:1rem">${escapeHtml(p.name)}</div>
                <div class="card-price" style="font-size:1rem">${escapeHtml(p.price)} BYN</div>
                <button class="btn-secondary" style="margin-top:10px;width:100%;font-size:0.8rem">Удалить</button>
            </div>
        `;
        div.querySelector('button').addEventListener('click', (e)=>{
            e.stopPropagation(); toggleFavoriteProduct({ ...p, _isFav: true });
        });
        div.addEventListener('click', ()=>showProductModal({...p, _isFav:true})); // Assume true in fav list
        list.appendChild(div);
    });
}

function renderMyProducts(products){
    const list = dom.myProducts;
    list.innerHTML = '';
    if(!products.length){ list.innerHTML = '<div style="color:#999;padding:10px">У вас нет объявлений</div>'; return; }

    products.forEach(p => {
        const row = document.createElement('div');
        row.className = 'list-row';
        // ИЗМЕНЕНИЕ НИЖЕ: Цена с BYN в строке
        row.innerHTML = `
            <div class="list-info">
                <img src="${p.imageUrl || 'https://via.placeholder.com/80'}" class="list-thumb">
                <div>
                    <div style="font-weight:600">${escapeHtml(p.name)}</div>
                    <div style="color:#777;font-size:0.9em">${escapeHtml(p.price)} BYN • ${escapeHtml(p.category)}</div>
                </div>
            </div>
            <div style="display:flex;gap:8px">
                <button class="btn-secondary btn-ghost" title="Изменить" data-edit><i class="fa-solid fa-pen"></i></button>
                <button class="btn-secondary btn-ghost" style="color:var(--danger)" title="Удалить" data-del><i class="fa-solid fa-trash"></i></button>
            </div>
        `;

        row.querySelector('[data-edit]').addEventListener('click', ()=>populateProductForm(p));
        row.querySelector('[data-del]').addEventListener('click', ()=>deleteProduct(p.id));
        list.appendChild(row);
    });
}

function populateProductForm(p){
    dom.productId.value = p.id;
    dom.productName.value = p.name;
    dom.productPrice.value = p.price;
    dom.productCategory.value = p.category;
    dom.productImage.value = p.imageUrl || '';
    dom.productForm.scrollIntoView({behavior:'smooth'});
    showToast('Данные загружены в форму');
}

async function deleteProduct(id){
    if(!confirm('Точно удалить объявление?')) return;
    try{
        const res = await fetch(`${API_ROOT}/products/${id}`, { method:'DELETE' });
        if(res.ok){
            showToast('Объявление удалено', 'success');
            loadProfile();
        } else throw new Error();
    }catch(e){ showToast('Ошибка удаления', 'error'); }
}

async function handleProductForm(e){
    e.preventDefault();
    if(!currentAccount){ showAuth(); return; }

    const id = dom.productId.value;
    const payload = {
        name: dom.productName.value.trim(),
        price: parseInt(dom.productPrice.value, 10) || 0,
        category: dom.productCategory.value.trim(),
        imageUrl: dom.productImage.value.trim(),
        account: { id: currentAccount.id }
    };

    if(!payload.name){ showToast('Введите название', 'error'); return; }

    try{
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_ROOT}/products/${id}` : `${API_ROOT}/products`;
        if(id) payload.id = parseInt(id);

        const res = await fetch(url, {
            method: method,
            headers:{'Content-Type':'application/json'},
            body: JSON.stringify(payload)
        });
        if(!res.ok) throw new Error((await res.text()) || 'Ошибка');

        showToast('Успешно сохранено!', 'success');
        clearProductForm();
        loadProfile();
    }catch(e){ showToast('Ошибка сохранения: ' + e.message, 'error'); }
}

function clearProductForm(){
    dom.productId.value = '';
    dom.productName.value = '';
    dom.productPrice.value = '';
    dom.productCategory.value = '';
    dom.productImage.value = '';
}

function show(el){ el.classList.remove('hidden'); }
function hide(el){ el.classList.add('hidden'); }
function escapeHtml(s){
    if(!s && s!==0) return '';
    return String(s).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'})[m]);
}