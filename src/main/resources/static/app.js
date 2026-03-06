const baseUrl = '/books?page=0&size=50';

async function fetchBooks() {

    const title = document.getElementById('filterTitle').value;
    const author = document.getElementById('filterAuthor').value;
    const dateFrom = document.getElementById('filterDateFrom').value;
    const dateTo = document.getElementById('filterDateTo').value;

    let url = baseUrl;

    if (title) url += `&title=${encodeURIComponent(title)}`;
    if (author) url += `&author=${encodeURIComponent(author)}`;
    if (dateFrom) url += `&publishDateFrom=${dateFrom}`;
    if (dateTo) url += `&publishDateTo=${dateTo}`;

    try {

        const res = await fetch(url);

        if (!res.ok)
            throw new Error('Error fetching books');

        const data = await res.json();

        renderBooks(data.books);

    } catch (err) {

        console.error(err);
        alert('Failed to load books');

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
        <td>${book.publishDate ?? ''}</td>
        <td>
            <button onclick="editBook(
                ${book.id},
                '${book.title}',
                '${book.author}',
                ${book.price},
                '${book.publishDate ?? ''}'
            )">Edit</button>

            <button onclick="deleteBook(${book.id})">Delete</button>
        </td>
        `;

        tbody.appendChild(tr);

    });

}

function editBook(id, title, author, price, publishDate) {

    document.getElementById('bookId').value = id;
    document.getElementById('bookTitle').value = title;
    document.getElementById('bookAuthor').value = author;
    document.getElementById('bookPrice').value = price;
    document.getElementById('bookPublishDate').value = publishDate;

}

async function saveBook() {

    const id = document.getElementById('bookId').value;

    const title = document.getElementById('bookTitle').value;
    const author = document.getElementById('bookAuthor').value;
    const price = parseFloat(document.getElementById('bookPrice').value);
    const publishDate = document.getElementById('bookPublishDate').value;

    const body = JSON.stringify({
        title,
        author,
        price,
        publishDate
    });

    try {

        let res;

        if (id) {

            res = await fetch(`/books/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body
            });

        } else {

            res = await fetch('/books', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body
            });

        }

        if (!res.ok)
            throw new Error('Error saving book');

        clearForm();

        fetchBooks();

    } catch (err) {

        console.error(err);
        alert('Failed to save book');

    }

}

function clearForm() {

    document.getElementById('bookId').value = '';
    document.getElementById('bookTitle').value = '';
    document.getElementById('bookAuthor').value = '';
    document.getElementById('bookPrice').value = '';
    document.getElementById('bookPublishDate').value = '';

}

function clearFilters() {

    document.getElementById('filterTitle').value = '';
    document.getElementById('filterAuthor').value = '';
    document.getElementById('filterDateFrom').value = '';
    document.getElementById('filterDateTo').value = '';

    fetchBooks();

}

async function deleteBook(id) {

    if (!confirm('Delete this book?'))
        return;

    try {

        const res = await fetch(`/books/${id}`, {
            method: 'DELETE'
        });

        if (!res.ok)
            throw new Error('Delete failed');

        fetchBooks();

    } catch (err) {

        console.error(err);
        alert('Failed to delete book');

    }

}

fetchBooks();