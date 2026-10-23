package com.example.fruteria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class ProductoAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

    // Guarda los elementos del diseño de cada fila.
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {

        val nombre = vista.findViewById<TextView>(R.id.nombreFila)
        val precio = vista.findViewById<TextView>(R.id.precioFila)
        val tipo = vista.findViewById<TextView>(R.id.tipoFila)
    }

    // Crea una fila a partir de su diseño XML.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.fila_producto, parent, false)

        return ViewHolder(vista)
    }

    // Coloca los datos del producto en la fila.
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val producto = productos[position]

        holder.nombre.text = producto.nombre

        holder.precio.text = String.format(
            Locale.getDefault(),
            "%.2f €/kg",
            producto.precio
        )

        holder.tipo.text = producto.tipo
    }

    // Devuelve el número de productos de la lista.
    override fun getItemCount(): Int {
        return productos.size
    }
}