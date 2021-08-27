package com.udacity.asteroidradar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.databinding.HarmlessAsteroidItemBinding
import com.udacity.asteroidradar.databinding.HazardousAsteroidItemBinding
import com.udacity.asteroidradar.models.Asteroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.ClassCastException

private const val HAZARDOUS_ASTEROID_ITEM = 0
private const val HARMLESS_ASTEROID_ITEM = 1


class AsteroidsListAdapter(val clickListener: AsteroidsListClickItemListener): ListAdapter<Asteroid, RecyclerView.ViewHolder>(AsteroidsListDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
//    fun updateItems(list: List<Asteroid>?) {
//        adapterScope.launch {
//            withContext(Dispatchers.Main) {
//                submitList(list)
//            }
//        }
//    }
    override fun getItemViewType(position: Int): Int {
        val dataItem = getItem(position) as Asteroid
        if(dataItem.isPotentiallyHazardous)
            return HAZARDOUS_ASTEROID_ITEM
        else
            return HARMLESS_ASTEROID_ITEM
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            HAZARDOUS_ASTEROID_ITEM->HazardousAsteroidViewHolder.from(parent)
            HARMLESS_ASTEROID_ITEM-> HarmlessAsteroidViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HarmlessAsteroidViewHolder -> {
                val dataItem = getItem(position) as Asteroid
                holder.bind(dataItem, clickListener)
            }
            is HazardousAsteroidViewHolder -> {
                val dataItem = getItem(position) as Asteroid
                holder.bind(dataItem, clickListener)
            }
            else -> throw ClassCastException("Unknown holderType ${holder::javaClass.name}")
        }
    }
}

class HarmlessAsteroidViewHolder(val binding: HarmlessAsteroidItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Asteroid, clickListener: AsteroidsListClickItemListener){
        binding.dataItem = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }
    companion object {
        fun from(parent: ViewGroup): HarmlessAsteroidViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = HarmlessAsteroidItemBinding.inflate(layoutInflater, parent, false)
            return HarmlessAsteroidViewHolder(binding)
        }
    }
}

class HazardousAsteroidViewHolder(val binding: HazardousAsteroidItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Asteroid, clickListener: AsteroidsListClickItemListener){
        binding.dataItem = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }
    companion object {
        fun from(parent: ViewGroup): HazardousAsteroidViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = HazardousAsteroidItemBinding.inflate(layoutInflater, parent, false)
            return HazardousAsteroidViewHolder(binding)
        }
    }
}

class AsteroidsListDiffCallback: DiffUtil.ItemCallback<Asteroid>(){
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }
}
// click item listener implementation
class AsteroidsListClickItemListener(val clickListener: (dataItem: Asteroid) -> Unit) {
    fun onClick(item: Asteroid) = clickListener(item)
}
