package de.jdynameta.metamodel.filter;


import org.junit.Assert;
import org.junit.Test;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOrExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.QueryCreator;
import de.jdynameta.base.test.PlantShopRepository;
import de.jdynameta.base.value.JdyPersistentException;

public class FilterCreatorTest
{

    @Test
    public void testCreateAppFilter() throws JdyPersistentException
    {
        ClassRepository plantShop = PlantShopRepository.createPlantShopRepository();
        ClassInfo plantType = plantShop.getClassForName(PlantShopRepository.Type.Plant.name());

        DefaultClassInfoQuery query = QueryCreator.start(plantType)
                .or()
                .equal("BotanicName", "Iris")
                .and()
                .greater("HeigthInCm", new Long(30L))
                .less("HeigthInCm", new Long(100L))
                .end()
                .end().query();

        FilterCreator creator = new FilterCreator();
        AppQuery appQuery = creator.createAppFilter(query);
        Assert.assertEquals("Query class info ", PlantShopRepository.Type.Plant.name(), appQuery.getClassName());
        Assert.assertEquals("Query repo info ", plantShop.getRepoName(), appQuery.getRepoName());
        Assert.assertNotNull("Query repo info ", appQuery.getFilterId());

        Assert.assertEquals("first epxr is or", AppOrExpr.class, appQuery.getExpr().getClass());
        Assert.assertEquals("first epxr in or is operator  ", AppOperatorExpr.class, ((AppOrExpr) appQuery.getExpr()).getOrSubExprColl().get(0).getClass());
        AppOperatorExpr firstOperator = (AppOperatorExpr) ((AppOrExpr) appQuery.getExpr()).getOrSubExprColl().get(0);
        Assert.assertEquals("BotanicName", firstOperator.getAttrName());
        Assert.assertEquals("compare value  ", "Iris", firstOperator.getTextVal());
        Assert.assertEquals("Operator type  ", AppOperatorEqual.class, firstOperator.getOperator().getClass());

        ClassInfoQuery newQuery = creator.createMetaFilter(appQuery, plantShop);
        Assert.assertEquals("Query class info ", PlantShopRepository.Type.Plant.name(), newQuery.getResultInfo().getInternalName());
        Assert.assertEquals("first epxr is or ", DefaultOrExpression.class, newQuery.getFilterExpression().getClass());

        DefaultOperatorExpression firstMetaOperator = (DefaultOperatorExpression) ((DefaultOrExpression) newQuery.getFilterExpression()).getExpressionIterator().next();
        Assert.assertEquals("compare value  ", "Iris", firstMetaOperator.getCompareValue());
        Assert.assertEquals("Operator type  ", DefaultOperatorEqual.getEqualInstance(), firstMetaOperator.getOperator());
        Assert.assertEquals("Operator type  ", "BotanicName", firstMetaOperator.getAttributeInfo().getInternalName());

    }

}
