package com.h1194.cuantracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore
import com.h1194.cuantracker.R;
import com.h1194.cuantracker.data.local.Transaction
import com.h1194.cuantracker.data.remote.FirestoreRepository
import com.h1194.cuantracker.databinding.FragmentHomeBinding;
import com.h1194.cuantracker.ui.adapter.HomeAdapter
import kotlinx.coroutines.launch
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date
import java.util.Locale;


class HomeFragment : Fragment() {

    private lateinit var firestoreRepository: FirestoreRepository
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var homeAdapter: HomeAdapter
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        firestoreRepository = FirestoreRepository()

        setupLineChart(binding.lineChart)
        setupRecyclerView()
        setupProfitCard()
        refreshData()

        return binding.root
    }

    private fun setupLineChart(lineChart: LineChart) {
        lifecycleScope.launch {
            try {
                val transactions = firestoreRepository.getAllTransactions()
                val calendar = Calendar.getInstance()
                val monthFormat = SimpleDateFormat("MMM", Locale("id"))
                val months = mutableListOf<String>()
                val pendapatanEntries = mutableListOf<Entry>()
                val pengeluaranEntries = mutableListOf<Entry>()

                for (i in 5 downTo 0) {
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - i)
                    val monthStr = monthFormat.format(calendar.time)
                    months.add(monthStr)

                    val pendapatan = transactions.filter {
                        it.type == "Pendapatan" && isInMonth(it.date, calendar)
                    }.sumByDouble { it.amount.toDouble() }

                    val pengeluaran = transactions.filter {
                        it.type == "Pengeluaran" && isInMonth(it.date, calendar)
                    }.sumByDouble { it.amount.toDouble() }

                    pendapatanEntries.add(Entry(5 - i.toFloat(), pendapatan.toFloat()))
                    pengeluaranEntries.add(Entry(5 - i.toFloat(), pengeluaran.toFloat()))
                }

                val pendapatanDataSet = LineDataSet(pendapatanEntries, "Pendapatan").apply {
                    color = resources.getColor(R.color.green)
                    setCircleColor(resources.getColor(R.color.green))
                    valueTextColor = resources.getColor(R.color.white)
                    valueTextSize = 12f
                    lineWidth = 2f
                }

                val pengeluaranDataSet = LineDataSet(pengeluaranEntries, "Pengeluaran").apply {
                    color = resources.getColor(R.color.red)
                    setCircleColor(resources.getColor(R.color.red))
                    valueTextColor = resources.getColor(R.color.white)
                    valueTextSize = 12f
                    lineWidth = 2f
                }

                lineChart.apply {
                    data = LineData(pendapatanDataSet, pengeluaranDataSet)
                    description.isEnabled = false
                    legend.textColor = resources.getColor(R.color.white)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = IndexAxisValueFormatter(months)
                        textColor = resources.getColor(R.color.white)
                        gridColor = resources.getColor(R.color.white)
                        granularity = 1f
                    }

                    axisLeft.apply {
                        textColor = resources.getColor(R.color.white)
                        gridColor = resources.getColor(R.color.white)
                    }

                    axisRight.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    animateX(1000)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal memuat grafik: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isInMonth(date: Date, calendar: Calendar): Boolean {
        val monthStart = calendar.clone() as Calendar
        monthStart.set(Calendar.DAY_OF_MONTH, 1)
        val monthEnd = calendar.clone() as Calendar
        monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))
        return date.after(monthStart.time) && date.before(monthEnd.time)
    }

    private fun setupProfitCard() {
        db.collection("transactions")
            .get()
            .addOnSuccessListener { documents ->
                val transactions = documents.mapNotNull { it.toObject(Transaction::class.java) }
                val pendapatan = transactions.filter { it.type == "Pendapatan" }.sumByDouble { it.amount.toDouble() }
                val pengeluaran = transactions.filter { it.type == "Pengeluaran" }.sumByDouble { it.amount.toDouble() }
                val profit = pendapatan - pengeluaran

                val formattedProfit = currencyFormatter.format(profit.toDouble()).replace("IDR", "Rp")
                binding.textViewProfit.text = "Keuntungan Bulan Ini: $formattedProfit"
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Gagal menghitung data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            try {
                val transactions = firestoreRepository.getAllTransactions()
                homeAdapter = HomeAdapter(transactions)
                binding.recyclerViewHistory.layoutManager = LinearLayoutManager(context)
                binding.recyclerViewHistory.adapter = homeAdapter
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                val newTransactions = firestoreRepository.getAllTransactions()
                homeAdapter.updateData(newTransactions)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal memperbarui data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

