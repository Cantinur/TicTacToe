package com.example.tictactoe.model
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.tictactoe.controller.PlayerModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tictactoe.R
import com.example.tictactoe.controller.Player
import kotlinx.android.synthetic.main.fragment_choose_player.*

class ChoosePlayerFragment : Fragment() {
    private var currentInstance: Int? = null
    private lateinit var playerModel: PlayerModel
    private var players: MutableList<String> = mutableListOf()
    private var selectedPlayer: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerModel = ViewModelProviders.of(this).get(PlayerModel::class.java)
        playerModel.allPlayers.observe(this, Observer {listOfPlayers ->
            val personAdapter = ArrayAdapter<String>(
                (activity as MainActivity),
                android.R.layout.simple_spinner_dropdown_item
            )
            personAdapter.clear()
            listOfPlayers
                .filter { player -> player.name != "Deep Thought" && player.name != "Marvin" && player.name != "Eddie"}  //Makes also sure that you can't select one of the tree AI players
                .forEach { player -> //Make sure that player 1 and player 2 can't be the same person
                    when(currentInstance != 2) {
                        true -> {
                            personAdapter.add(player.name)
                            players.add(player.name)
                        }
                        false -> if ((activity as MainActivity).getPlayerOne() != player.name){
                            personAdapter.add(player.name)
                            players.add(player.name)
                        }
                }
            }
            choose_player_spinner.adapter = personAdapter

            if (selectedPlayer != null) {
                choose_player_spinner.setSelection(personAdapter.getPosition(selectedPlayer))
            }else if (choose_player_spinner.childCount > 0){
                selectedPlayer = players[0]
            }
        })
        setTextOnScreen()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*ADD NEW PLAYER BUTTON PRESS*/
        add_player_text.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                handleText()
                return@OnKeyListener true
            }
            false
        })

        add_player_button.setOnClickListener { handleText() }

        /*TO NEXT FRAGMENT*/
        bottom_button_on_player_select.setOnClickListener {
            if (add_player_text.text.isNotEmpty() && doesNotContain()) {
                handleText()
                goToNextFragment()
            } else if ( choose_player_spinner.selectedItem != null ) {
                goToNextFragment()
            }
        }
    }

    fun setInstance(instance: Int){ currentInstance = instance }

    private fun goToNextFragment(){
        selectedPlayer = selectedPlayer ?: choose_player_spinner.selectedItem!! as String
        val mainActivity = (activity as MainActivity)
        when (currentInstance) {
            1 -> mainActivity.setUpPlayerTwo(selectedPlayer!!)
            2 -> mainActivity.goToPVPGame(selectedPlayer!!)
            3 -> mainActivity.setUpAI(selectedPlayer!!)
            else-> println("Error accord")
        }
    }

    private fun doesNotContain():Boolean = players.none { (it == add_player_text.text.toString()) }

    private fun handleText() {
        if (add_player_text.text.isNotEmpty() && doesNotContain()) {
            val playerName = add_player_text.text.toString().capitalize()
            playerModel.insert( Player(playerName, 0, 0) )
            selectedPlayer = playerName
            mainLayout.requestFocus()
        }
        add_player_text.clearFocus()
        add_player_text.text.clear()
        add_player_text.clearAnimation()
    }

    private fun setTextOnScreen(){
        when (currentInstance ){
            1 -> {
                top_text.text = getString(R.string.choose_player1)
                bottom_button_on_player_select.text = getString(R.string.choose_player2)
            }
            2 -> {
                top_text.text = getString(R.string.choose_player2)
                bottom_button_on_player_select.text = getString(R.string.play_game)
            }
            3 -> {
                top_text.text = getString(R.string.choose_player)
                bottom_button_on_player_select.text = getString(R.string.choose_ai)
            }
        }
    }
}
