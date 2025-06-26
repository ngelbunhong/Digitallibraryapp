package com.library.digitallibrary.ui.home.book

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.ui.theme.DigitalLibraryTheme

/**
 * The main UI for the book collection screen.
 * It displays a grid of books using LazyVerticalGrid.
 */
@Composable
fun BookScreen(
    books: List<Book>,
    onBookClick: (Book) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = books,
            key = { it.id } // Use the book ID as a stable key for performance.
        ) { book ->
            BookGridItem(
                book = book,
                onBookClick = onBookClick
            )
        }
    }
}

/**
 * A Composable that displays a single book item in the grid.
 */
@Composable
fun BookGridItem(
    book: Book,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onBookClick(book) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                // Use the 'thumbnail' property from your Book data class.
                model = book.thumbnail,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    // Use a taller aspect ratio for book covers.
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookScreenPreview() {
    // Create some fake book data for the preview.
    val fakeBooks = List(10) {
        Book(
            id = it,
            title = "Book Title That Is Quite Long $it",
            author = "Author Name",
            thumbnail = "https://picsum.photos/seed/book${it}/400/600",
            year = 2024,
            isAvailable = true,
            tags = listOf("Fiction"),
            createdAtTimestamp = 1705302000000
        )
    }
    DigitalLibraryTheme {
        BookScreen(books = fakeBooks, onBookClick = {})
    }
}
