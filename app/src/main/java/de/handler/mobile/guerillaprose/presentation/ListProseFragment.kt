package de.handler.mobile.guerillaprose.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.squareup.picasso.Picasso
import de.handler.mobile.guerillaprose.R
import de.handler.mobile.guerillaprose.data.GuerillaProseRepository
import kotlinx.android.synthetic.main.fragment_list_prose.*
import org.koin.android.ext.android.inject

class ListProseFragment : Fragment() {
    private lateinit var picasso: Picasso
    private val repository: GuerillaProseRepository by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_prose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        picasso = Picasso.Builder(context).build()

        val adapter = recyclerView.adapter as? GuerillaProseAdapter
                ?: GuerillaProseAdapter(picasso)
        recyclerView.adapter = adapter

        repository.getGuerillaProses().observe(this, Observer {
            if (it.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            adapter.submitList(it)
        })

        val navController = view.findNavController()
        createGuerillaProseFab.setOnClickListener {
            navController.navigate(R.id.actionCreateProse)
        }
    }
}
