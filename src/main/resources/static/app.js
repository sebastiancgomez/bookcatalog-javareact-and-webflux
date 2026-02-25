const apiUrl = '/books?page=0&size=50'; // ajusta tamaño si quieres paginación real

async function fetchBooks() {
    const title = document.getElementById('filterTitle').value;
    const author = document.getElementById('filterAuthor').value;

    let url = `/books?page=0&size=50`;
    if (title) url += `&title=${encodeURIComponent(title)}`;
    if (author) url += `&author=${encodeURIComponent(author)}`;

    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error('Error fetching books');

        const data = await res.json();
        renderBooks(data.books);
    } catch (err) {
        console.error(err);
        alert('Failed to load books. Check backend.');
    }
}

function renderBooks(books) {
    const tbody = document.getElementById('booksTable');
    tbody.innerHTML = '';
    books.forEach(book => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${book.id}</td>
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td>${book.price}</td>
            <td>
                <button onclick="editBook(${book.id}, '${book.title}', '${book.author}', ${book.price})">Edit</button>
                <button onclick="deleteBook(${book.id})">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function editBook(id, title, author, price) {
    document.getElementById('bookId').value = id;
    document.getElementById('bookTitle').value = title;
    document.getElementById('bookAuthor').value = author;
    document.getElementById('bookPrice').value = price;
}

async function saveBook() {
    const id = document.getElementById('bookId').value;
    const title = document.getElementById('bookTitle').value;
    const author = document.getElementById('bookAuthor').value;
    const price = parseFloat(document.getElementById('bookPrice').value);

    const body = JSON.stringify({ title, author, price });

    try {
        let res;
        if (id) {
            // Update
            res = await fetch(`/books/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body
            });
        } else {
            // Create
            res = await fetch('/books', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body
            });
        }

        if (!res.ok) throw new Error('Error saving book');

        // Clear form
        document.getElementById('bookId').value = '';
        document.getElementById('bookTitle').value = '';
        document.getElementById('bookAuthor').value = '';
        document.getElementById('bookPrice').value = '';

        fetchBooks();
    } catch (err) {
        console.error(err);
        alert('Failed to save book.');
    }
}

async function deleteBook(id) {
    if (!confirm('Delete this book?')) return;
    try {
        const res = await fetch(`/books/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error deleting book');
        fetchBooks();
    } catch (err) {
        console.error(err);
        alert('Failed to delete book.');
    }
}

// Load books initially
fetchBooks();