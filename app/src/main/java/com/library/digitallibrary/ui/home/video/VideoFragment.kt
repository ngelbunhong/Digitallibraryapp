package com.library.digitallibrary.ui.home.video

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.digitallibrary.MainActivity
import com.library.digitallibrary.R
import com.library.digitallibrary.ui.theme.DigitalLibraryTheme

class VideoFragment : Fragment() {

    // 1. Get the ViewModel instance
    // This `by viewModels()` delegate creates and manages the ViewModel for this Fragment.
    private val viewModel: VideoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Instead of inflating an XML file, we create a ComposeView.
        return ComposeView(requireContext()).apply {

            // The `setContent` block is where you build your Compose UI.
            setContent {
                // 2. Collect data from the ViewModel
                // `collectAsStateWithLifecycle` safely observes the `StateFlow` from the
                // ViewModel and turns it into a Compose State. The `videos` variable
                // will automatically update whenever the data in the ViewModel changes.
                val videos by viewModel.videos.collectAsStateWithLifecycle()

                // 3. Call your UI (the Composable Screen)
                // We wrap it in our theme and pass the data and click handlers.
                DigitalLibraryTheme {
                    VideoScreen(
                        videos = videos,
                        onVideoClick = { video ->
                            // This is the logic that runs when a video is clicked.
                            // You can navigate to a detail screen here later.
                            Toast.makeText(
                                requireContext(),
                                "You clicked on: ${video.title}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val title = getString(R.string.collection_videos)
        (activity as? MainActivity)?.updateToolbar(MainActivity.ToolbarState.InnerScreen(title))
    }
}