package com.library.digitallibrary.ui.home.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.ui.theme.DigitalLibraryTheme


/**
 * The main UI for the video collection screen.
 * It displays a grid of videos using LazyVerticalGrid.
 */
@Composable
fun VideoScreen(
    videos: List<Video>,
    onVideoClick: (Video) -> Unit
) {
    // LazyVerticalGrid is perfect for showing items in a grid.
    // GridCells.Adaptive automatically fits as many columns as possible based on the minimum size.
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = videos, key = { it.id }) { video ->
            VideoGridItem(video = video, onVideClick = onVideoClick)
        }
    }
}

/**
 * A Composable that displays a single video item in the grid.
 */
@Composable
fun VideoGridItem(
    video: Video,
    onVideClick: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onVideClick(video) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f), // Standard video aspect ratio
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.author,
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
fun VideoScreenPreview() {
    val fakeVideos = List(10) {
        Video(
            id = it,
            title = "Video Title That Is Quite Long $it",
            author = "Author Name",
            thumbnailUrl = "https://picsum.photos/seed/video103/400/225",
            duration = "15:05",
            createdAtTimestamp = 1705302000000,
            downloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        )
    }
    DigitalLibraryTheme {
        VideoScreen(videos = fakeVideos, onVideoClick = {})
    }
}