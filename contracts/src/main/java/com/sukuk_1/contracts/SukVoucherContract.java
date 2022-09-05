package com.sukuk_1.contracts;

import com.sukuk_1.states.Suk;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class SukVoucherContract implements Contract {

    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.sukuk_1.contracts.SukVoucherContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        //Extract the command from the transaction.
        final CommandData commandData = tx.getCommands().get(0).getValue();

        //Verify the transaction according to the intention of the transaction
        if (commandData instanceof SukVoucherContract.Commands.Issue) {
            Suk output = tx.outputsOfType(Suk.class).get(0);
            requireThat(require -> {
                require.using("This transaction should only have one Suk Voucher state as output", tx.getOutputs().size() == 1);
                require.using("The output Suk state should have clear description", !output.getVoucherDesc().equals(""));
                return null;
            });
        } else if (commandData instanceof SukContract.Commands.Redeem) {
            //Transaction verification will happen in SukContract Contract
        } else {
            //Unrecognized Command type
            throw new IllegalArgumentException("Incorrect type of Suk Commands");
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        //In our hello-world app, We will have two commands.
        class Issue implements SukVoucherContract.Commands {
        }
    }
}
