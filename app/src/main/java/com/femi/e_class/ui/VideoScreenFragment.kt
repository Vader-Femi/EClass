package com.femi.e_class.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.femi.e_class.KEY_AVATAR_URL
import com.femi.e_class.KEY_COURSE_CODE
import com.femi.e_class.KEY_ROOM_NAME
import com.femi.e_class.databinding.FragmentVideoScreenBinding
import com.femi.e_class.viewmodels.HomeActivityViewModel
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.jitsi.meet.sdk.JitsiMeetView
import java.net.URL

class VideoScreenFragment : Fragment() {

    private var _binding: FragmentVideoScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<HomeActivityViewModel>()
    private var roomName = ""
    private var courseCode = ""
    private var avatarURL = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null && context != null) {

            roomName = requireArguments().getString(KEY_ROOM_NAME) ?: "NoName"
            courseCode = requireArguments().getString(KEY_COURSE_CODE) ?: ""
            avatarURL = requireArguments().getString(KEY_AVATAR_URL) ?: ""

            val jitsiView = JitsiMeetView(requireActivity())
            val options: JitsiMeetConferenceOptions = JitsiMeetConferenceOptions.Builder()
                .setServerURL(URL("https://meet.jit.si"))
                .setRoom("LASU-$roomName-$courseCode")
                .setUserInfo(JitsiMeetUserInfo(
                    Bundle().apply {
                        this.putString("displayName", viewModel.firstName)
                        this.putString("email", viewModel.matric)
                    }
                ))
                .setAudioMuted(false)
                .setVideoMuted(false)
                .setAudioOnly(false)
                .build()

            jitsiView.join(options)
            requireActivity().setContentView(jitsiView)

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}