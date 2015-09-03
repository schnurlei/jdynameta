package de.jdynameta.metamodel.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.AndExpression;
import de.jdynameta.base.metainfo.filter.AssociationExpression;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.ExpressionPrimitiveOperator;
import de.jdynameta.base.metainfo.filter.ExpressionVisitor;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.ObjectReferenceEqualExpression;
import de.jdynameta.base.metainfo.filter.ObjectReferenceSubqueryExpression;
import de.jdynameta.base.metainfo.filter.OperatorEqual;
import de.jdynameta.base.metainfo.filter.OperatorExpression;
import de.jdynameta.base.metainfo.filter.OperatorGreater;
import de.jdynameta.base.metainfo.filter.OperatorLess;
import de.jdynameta.base.metainfo.filter.OperatorVisitor;
import de.jdynameta.base.metainfo.filter.OrExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultExpressionAnd;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorGreater;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorLess;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOrExpression;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.value.JdyPersistentException;

public class FilterCreator implements ExpressionVisitor
{
	private long idCounter = 0;
	private AppFilterExpr curExpr;
	
	public void resetIdCounter()
	{
		this.idCounter = 0;
	}
	
	public AppQuery createAppFilter(ClassInfoQuery metaQuery) throws JdyPersistentException
	{
		AppQuery appQuery = new AppQuery();
		appQuery.setFilterId(nextId());
		appQuery.setRepoName(metaQuery.getResultInfo().getRepoName());
		appQuery.setClassName(metaQuery.getResultInfo().getInternalName());
		appQuery.setExpr(createAppExpr(metaQuery.getFilterExpression()));
		
		
		return appQuery;
	}

	public AppFilterExpr createAppExpr( ObjectFilterExpression aMetaExpr) throws JdyPersistentException 
	{
		aMetaExpr.visit(this);

		AppFilterExpr result = curExpr;
		
		return result;
	}
	
	private long nextId()
	{
		return this.idCounter++;
	}
	
	@Override
	public void visitAndExpression(AndExpression aAndExpr)	throws JdyPersistentException 
	{
		AppAndExpr andExpr = new AppAndExpr();
		andExpr.setExprId(nextId());
		ChangeableObjectList<AppFilterExpr> subExprs = new ChangeableObjectList<AppFilterExpr>();
		
		for( Iterator<ObjectFilterExpression> exprIter = aAndExpr.getExpressionIterator(); exprIter.hasNext(); ) {
			ObjectFilterExpression subMetaexpr = exprIter.next();
			AppFilterExpr subAppExpr = createAppExpr(subMetaexpr);
			subAppExpr.setAppAndExpr((AppAndExpr) andExpr);
			subExprs.addObject(subAppExpr);
		}
		
		andExpr.setAndSubExprColl(subExprs );
		
		curExpr = andExpr;
	}

	@Override
	public void visitOrExpression(OrExpression aOrExpression) throws JdyPersistentException 
	{
		AppOrExpr orExpr = new AppOrExpr();
		orExpr.setExprId(nextId());
		ChangeableObjectList<AppFilterExpr> subExprs = new ChangeableObjectList<AppFilterExpr>();
		
		for( Iterator<ObjectFilterExpression> exprIter = aOrExpression.getExpressionIterator(); exprIter.hasNext(); ) {
			ObjectFilterExpression subMetaexpr = exprIter.next();
			AppFilterExpr subAppExpr = createAppExpr(subMetaexpr);
			subAppExpr.setAppOrExpr(orExpr);
			subExprs.addObject(subAppExpr);
		}
		
		orExpr.setOrSubExprColl(subExprs );
		
		curExpr = orExpr;
	}

	@Override
	public void visitOperatorExpression(OperatorExpression aOpExpr)	throws JdyPersistentException 
	{
		AppOperatorExpr appOpExpr = new AppOperatorExpr();
		appOpExpr.setExprId(nextId());
		appOpExpr.setAttrName(aOpExpr.getAttributeInfo().getInternalName());
		appOpExpr.setOperator(createAppOperator(aOpExpr.getOperator()));
		
		setAppCompareValue(appOpExpr, aOpExpr);
		
		curExpr = appOpExpr;
	}

	@Override
	public void visitReferenceEqualExpression(ObjectReferenceEqualExpression aOpExpr) throws JdyPersistentException 
	{
		throw new UnsupportedOperationException("Not impemented yet");
	}

	@Override
	public void visitAssociationExpression(AssociationExpression aOpExpr) throws JdyPersistentException 
	{
		throw new UnsupportedOperationException("Not impemented yet");
	}

	@Override
	public void visitReferenceQueryExpr( ObjectReferenceSubqueryExpression aExpression)	throws JdyPersistentException 
	{
		throw new UnsupportedOperationException("Not impemented yet");
	}
	
	private void setAppCompareValue(AppOperatorExpr appOpExpr,	OperatorExpression aOpExpr) 
	{
		PrimitiveAttributeInfo aMetaPrim = aOpExpr.getAttributeInfo();
		
		if(aMetaPrim.getType() instanceof BlobType) {
			throw new RuntimeException("Invalid type " + aMetaPrim.getType());
		} else if(aMetaPrim.getType() instanceof BooleanType) 
		{
			appOpExpr.setBooleanVal((Boolean) aOpExpr.getCompareValue());
		} else if(aMetaPrim.getType() instanceof CurrencyType) 
		{
			appOpExpr.setDecimalVal((BigDecimal) aOpExpr.getCompareValue());
		} else if(aMetaPrim.getType() instanceof FloatType) 
		{
			appOpExpr.setFloatVal((Double) aOpExpr.getCompareValue());
		} else if(aMetaPrim.getType() instanceof LongType) 
		{
			appOpExpr.setLongVal((Long) aOpExpr.getCompareValue());
		} else if(aMetaPrim.getType() instanceof TextType) 
		{
			appOpExpr.setTextVal((String) aOpExpr.getCompareValue());
		} else if(aMetaPrim.getType() instanceof TimeStampType) 
		{
			appOpExpr.setTimestampVal((Date) aOpExpr.getCompareValue());
		} else if(aMetaPrim.getType() instanceof VarCharType) 
		{
			throw new RuntimeException("Invalid type " + aMetaPrim.getType());
		} else {
			throw new RuntimeException("Invalid type " + aMetaPrim.getType());
		}		
	}

	
	
	private AppPrimitiveOperator createAppOperator( ExpressionPrimitiveOperator aMetaOper) throws JdyPersistentException 
	{
		OperatorVisitorImplementation aVisitor = new OperatorVisitorImplementation();
		aVisitor.init();
		aMetaOper.visitOperatorHandler(aVisitor );
		
		return aVisitor.getResultOp();
	}

		
	/**
	 * Convert a ClassInfoQuery object from a application query object
	 * @param appQuery
	 * @param repo
	 * @return
	 * @throws JdyPersistentException
	 */
	public ClassInfoQuery createMetaFilter(AppQuery appQuery, ClassRepository repo) throws JdyPersistentException
	{
		ClassInfo typeInfo = repo.getClassForName(appQuery.getClassName());
		assert(repo.getRepoName().equals(typeInfo.getRepoName()) );
		
		
		DefaultClassInfoQuery metaQuery = new DefaultClassInfoQuery(typeInfo);
		metaQuery.setFilterExpression(createMetaExpr(appQuery.getExpr(), typeInfo));
		
		
		return metaQuery;
	}

	private ObjectFilterExpression createMetaExpr(AppFilterExpr expr, ClassInfo typeInfo) 
	{
		ObjectFilterExpression result = null;
		
		if (expr instanceof AppOperatorExpr) 
		{
			AppOperatorExpr opratorExpr = (AppOperatorExpr) expr;
			DefaultOperatorExpression metaExpr = new DefaultOperatorExpression();
			PrimitiveAttributeInfo attr = (PrimitiveAttributeInfo) typeInfo.getAttributeInfoForExternalName(opratorExpr.getAttrName());
			metaExpr.setAttributeInfo(attr);
			metaExpr.setCompareValue(getMetaCompareValue(opratorExpr, attr));
			metaExpr.setMyOperator(createMetaOperator(opratorExpr.getOperator()));
			result = metaExpr;
		} else if (expr instanceof AppAndExpr) 
		{
			ArrayList<ObjectFilterExpression> subExprs = new ArrayList<ObjectFilterExpression>();
			for(Object appSubExpr : ((AppAndExpr) expr).getAndSubExprColl() )
			{
				subExprs.add(createMetaExpr((AppFilterExpr) appSubExpr, typeInfo));
			}
			result = new DefaultExpressionAnd(subExprs);
		} else if (expr instanceof AppOrExpr) 
		{
			ArrayList<ObjectFilterExpression> subExprs = new ArrayList<ObjectFilterExpression>();
			for(Object appSubExpr : ((AppOrExpr) expr).getOrSubExprColl() )
			{
				subExprs.add(createMetaExpr((AppFilterExpr) appSubExpr, typeInfo));
			}
			result = new DefaultOrExpression(subExprs);
		} else {
			throw new UnsupportedOperationException("Unknown expression type " + expr.getClass());
		}
		
		return result;
	}

	private Object getMetaCompareValue(AppOperatorExpr appOpExpr,	PrimitiveAttributeInfo aMetaPrim) 
	{
		if(aMetaPrim.getType() instanceof BlobType) 
		{
			throw new RuntimeException("Invalid type " + aMetaPrim.getType());
		} else if(aMetaPrim.getType() instanceof BooleanType) 
		{
			return appOpExpr.getBooleanVal();
		} else if(aMetaPrim.getType() instanceof CurrencyType) 
		{
			return appOpExpr.getDecimalVal();
		} else if(aMetaPrim.getType() instanceof FloatType) 
		{
			return appOpExpr.getFloatVal();
		} else if(aMetaPrim.getType() instanceof LongType) 
		{
			return appOpExpr.getLongVal();
		} else if(aMetaPrim.getType() instanceof TextType) 
		{
			return appOpExpr.getTextVal();
		} else if(aMetaPrim.getType() instanceof TimeStampType) 
		{
			return appOpExpr.getTimestampVal();
		} else if(aMetaPrim.getType() instanceof VarCharType) 
		{
			throw new RuntimeException("Invalid type " + aMetaPrim.getType());
		} else {
			throw new RuntimeException("Invalid type " + aMetaPrim.getType());
		}			
	}

	private ExpressionPrimitiveOperator createMetaOperator(	AppPrimitiveOperator anAppOperator) 
	{
		ExpressionPrimitiveOperator result = null;
		if( anAppOperator instanceof AppOperatorEqual) 
		{
			result = (((AppOperatorEqual)anAppOperator).getIsNotEqual()) ? DefaultOperatorEqual.getNotEqualInstance() 
					: DefaultOperatorEqual.getEqualInstance();
		} else if( anAppOperator instanceof AppOperatorGreater) {
			
			result = (((AppOperatorGreater)anAppOperator).getIsAlsoEqual()) ? DefaultOperatorGreater.getGreaterOrEqualInstance() 
					: DefaultOperatorGreater.getGreateInstance();
		} else if( anAppOperator instanceof AppOperatorLess) 
		{
			result = (((AppOperatorLess)anAppOperator).getIsAlsoEqual()) ? DefaultOperatorLess.getLessOrEqualInstance() 
					: DefaultOperatorLess.getLessInstance();
		}
		return result;
	}

	
	private final static class OperatorVisitorImplementation implements OperatorVisitor 
	{
		private AppPrimitiveOperator resultOp;
	
		private void init() {
			resultOp = null;
		}
		
		public AppPrimitiveOperator getResultOp() 
		{
			return resultOp;
		}
		
		@Override
		public void visitOperatorLess(OperatorLess aOperator) 
		{
			AppOperatorLess appOp = new AppOperatorLess();
			appOp.setIsAlsoEqual(aOperator.isAlsoEqual());
			resultOp = appOp;
		}
		
		@Override
		public void visitOperatorGreater(OperatorGreater aOperator) 
		{
			AppOperatorGreater appOp = new AppOperatorGreater();
			appOp.setIsAlsoEqual(aOperator.isAlsoEqual());
			resultOp = appOp;
		}
		
		@Override
		public void visitOperatorEqual(OperatorEqual aOperator) 
		{
			AppOperatorEqual appOp = new AppOperatorEqual();
			appOp.setIsNotEqual(aOperator.isNotEqual());
			resultOp = appOp;
		}
	}
	
	
	public static HashMap<String, String> createAbbreviationMap() {
		HashMap<String, String> att2AbbrMap = new HashMap<String, String>();
		att2AbbrMap.put("repoName", "rn");
		att2AbbrMap.put("className", "cn");
		att2AbbrMap.put("expr", "ex");
		att2AbbrMap.put("orSubExpr", "ose");
		att2AbbrMap.put("andSubExpr", "ase");
		att2AbbrMap.put("attrName", "an");
		att2AbbrMap.put("operator", "op");
		att2AbbrMap.put("isNotEqual", "ne");
		att2AbbrMap.put("isAlsoEqual", "ae");
		att2AbbrMap.put("longVal", "lv");
		att2AbbrMap.put("textVal", "tv");
		return att2AbbrMap;
	}
	
}
