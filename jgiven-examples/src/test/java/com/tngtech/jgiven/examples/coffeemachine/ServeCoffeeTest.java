package com.tngtech.jgiven.examples.coffeemachine;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.examples.coffeemachine.steps.GivenCoffee;
import com.tngtech.jgiven.examples.coffeemachine.steps.ThenCoffee;
import com.tngtech.jgiven.examples.coffeemachine.steps.WhenCoffee;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.tags.FeatureDataTables;
import com.tngtech.jgiven.tags.Issue;

/**
 * Original example due to Cucumber Wiki.
 */
@RunWith( DataProviderRunner.class )
@Description( "In order to refresh myself</br>" +
        "as a customer</br>" +
        "I want coffee to be served" )
public class ServeCoffeeTest extends ScenarioTest<GivenCoffee, WhenCoffee, ThenCoffee> {

    @Test
    public void an_empty_coffee_machine_cannot_serve_any_coffee() throws Exception {

        given().an_empty_coffee_machine();

        when().I_insert_$_one_euro_coins( 5 )
            .and().I_press_the_coffee_button();

        then().an_error_should_be_shown()
            .and().no_coffee_should_be_served();
    }

    @Test
    public void no_coffee_left_error_is_shown_when_there_is_no_coffee_left() {
        given().an_empty_coffee_machine();
        when().I_insert_$_one_euro_coins( 5 )
            .and().I_press_the_coffee_button();
        then().the_message_$_is_shown( "Error: No coffees left" );
    }

    @Test
    public void not_enough_money_message_is_shown_when_insufficient_money_was_given() throws Exception {

        given().a_coffee_machine()
            .and().there_are_$_coffees_left_in_the_machine( 2 );
        when().I_insert_$_one_euro_coins( 1 )
            .and().I_press_the_coffee_button();
        then().the_message_$_is_shown( "Error: Insufficient money" );
    }

    @Test
    @FeatureDataTables
    @DataProvider( {
        "0, 0, Error: No coffees left",
        "0, 1, Error: No coffees left",
        "1, 0, Error: Insufficient money",
        "0, 5, Error: No coffees left",
        "1, 5, Enjoy your coffee!",
    } )
    public void correct_messages_are_shown( int coffeesLeft, int numberOfCoins, String message ) throws Exception {
        given().a_coffee_machine()
            .and().there_are_$_coffees_left_in_the_machine( coffeesLeft );
        when().I_insert_$_one_euro_coins( numberOfCoins )
            .and().I_press_the_coffee_button();
        then().the_message_$_is_shown( message );
    }

    @Test
    @FeatureDataTables
    @Issue( "#15" )
    @DataProvider( { "1", "3", "10" } )
    public void serving_a_coffee_reduces_the_number_of_available_coffees_by_one( int initialCoffees ) {
        given().a_coffee_machine()
            .and().there_are_$_coffees_left_in_the_machine( initialCoffees );
        when().I_insert_$_one_euro_coins( 2 )
            .and().I_press_the_coffee_button();
        then().a_coffee_should_be_served()
            .and().there_are_$_coffees_left_in_the_machine( initialCoffees - 1 );
    }

    @Test
    public void a_turned_off_coffee_machine_cannot_serve_coffee() throws Exception {

        given().a_coffee_machine()
            .and().the_machine_is_turned_off();

        when().I_press_the_coffee_button();

        then().no_coffee_should_be_served();

    }

    @Test
    @DataProvider( {
        "true, 1, 1, false",
        "true, 1, 2, true",
        "true, 0, 2, false",
        "false, 1, 2, false",
    } )
    public void buy_a_coffee( boolean onOrOff, int coffees, int dollars, boolean shouldOrShouldNot ) {

        given().a_coffee_machine().
            and().there_are_$_coffees_left_in_the_machine( coffees ).
            and().the_machine_is_$on_or_off$( onOrOff ).
            and().the_coffee_costs_$_dollar( 2 );

        when().I_insert_$_one_euro_coins( dollars ).
            and().I_press_the_coffee_button();

        then().I_$should_or_should_not$_be_served_a_coffee( shouldOrShouldNot );
    }
}
