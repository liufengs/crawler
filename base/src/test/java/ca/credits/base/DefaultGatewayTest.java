package ca.credits.base;

import ca.credits.base.event.AbstractEvent;
import ca.credits.base.gateway.DefaultGateway;
import ca.credits.base.gateway.IGateway;
import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwen on 16/8/26.
 */
@Slf4j
public class DefaultGatewayTest {
    @Test
    public void testSuggest() {

        Executive one = new Executive("one",null, IExecutive.Status.DONE);

        Executive two = new Executive("two",null, IExecutive.Status.EXCEPTION);

        Executive three = new Executive("three",null, IExecutive.Status.DONE);

        Executive four = new Executive("four",null, IExecutive.Status.EXCEPTION);


        final List<IExecutive> parents = new ArrayList<IExecutive>(){{
            add(one);
            add(two);
            add(three);
            add(four);
        }};

        Executive executive = new Executive("self",parents, IExecutive.Status.UNDO);
        IGateway gateway = new DefaultGateway("one || two || three || four");
        Assert.assertEquals(gateway.suggest(executive), IGateway.GatewaySuggest.NEXT);

        gateway = new DefaultGateway("one && two || three || four");
        Assert.assertEquals(gateway.suggest(executive), IGateway.GatewaySuggest.NEXT);

        gateway = new DefaultGateway("one && two && three || four");
        Assert.assertEquals(gateway.suggest(executive), IGateway.GatewaySuggest.EXCEPTION);

        gateway = new DefaultGateway("one && two && three && four");
        Assert.assertEquals(gateway.suggest(executive), IGateway.GatewaySuggest.EXCEPTION);

        gateway = new DefaultGateway("(one && two) && (three && four)");
        Assert.assertEquals(gateway.suggest(executive), IGateway.GatewaySuggest.EXCEPTION);

        gateway = new DefaultGateway("((((one && two) && (three && four))))");
        Assert.assertEquals(gateway.suggest(executive), IGateway.GatewaySuggest.EXCEPTION);

        gateway = new DefaultGateway("one || ((two) && (three && four))");
        Assert.assertEquals(gateway.suggest(executive), IGateway.GatewaySuggest.NEXT);
    }


    @Test
    public void testParsii() throws ParseException {
        Expression expression = Parser.parse("1||(0&&0)");

        log.info(String.valueOf(expression.evaluate()));
    }

    class Executive extends AbstractEvent {
        public Executive(String id,List<IExecutive> parents,Status status){
            super("loggerEngine","test","event",null,null,null);
            this.id = id;
            this.parents = parents;
            this.status.set(status.ordinal());
        }
    }
}
