package com.sukuk_1.contracts;

import com.sukuk_1.states.Suk;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class SukContract implements Contract {

    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.sukuk_1.contracts.SukContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        //Extract the command from the transaction.
        final CommandData commandData = tx.getCommands().get(0).getValue();

        if (commandData instanceof Commands.Create) {
            com.sukuk_1.states.SukContract output = tx.outputsOfType(com.sukuk_1.states.SukContract.class).get(0);
            requireThat(require -> {
                require.using("This transaction should only output one SukContract state", tx.getOutputs().size() == 1);
                require.using("The output SukContract state should have clear description of Apple product", !output.getDescription().equals(""));
                require.using("The output SukContract state should have non zero weight", output.getValue() > 0);
                return null;
            });
        } else if (commandData instanceof SukContract.Commands.Redeem) {
            //Retrieve the output state of the transaction
            Suk input = tx.inputsOfType(Suk.class).get(0);
            com.sukuk_1.states.SukContract output = tx.outputsOfType(com.sukuk_1.states.SukContract.class).get(0);

            //Using Corda DSL function requireThat to replicate conditions-checks
            requireThat(require -> {
                require.using("This transaction should consume two states", tx.getInputStates().size() == 2);
                require.using("The issuer of the Apple stamp should be the producing farm of this basket of apple", input.getIssuer().equals(output.getIssuer()));
                require.using("The basket of apple has to weight more than 0", output.getValue() > 0);
                return null;
            });
        } else {
            //Unrecognized Command type
            throw new IllegalArgumentException("Incorrect type of SukContract Commands");
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Create implements SukContract.Commands {
        }

        class Redeem implements SukContract.Commands {
        }
    }
}
