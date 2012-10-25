train <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\trainingTest.txt"));
test <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\testingTest.txt"));
svm.model <- svm(train[,1025] ~ .,data=train,cost=100,gamma=1)
svm.pred <- predict(svm.model, test[,-1025])
output <- table(pred = svm.pred, true = test[,-1025])



data <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\4charGramCosineMatrix.txt", header=FALSE))
out = kmeans(data,center=15,nstart=10000)
mlabels <- scan("c:\\users\\dunng\\workspace\\greektext\\output\\manuscriptLabels.txt",what=character(),sep=",",nlines=1)
hresult <- hclust(dist(data))
plot(hresult,labels=mlabels)
compositedata <- as.matrix(read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixFull.txt"))

library(vegan)
CompositeGramIDFFeatureMatrixFullOld.txt
compfull <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixFull.txt", header=FALSE)
mlabels <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\manuscriptLabels.txt",what=character(),sep=",",nlines=1)
clabels <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramFeatureVectorFull.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
data.dis <- vegdist(data)
data.mds0 <- monoMDS(data.dis)
stressplot(data.mds0, data.dis)
ordiplot(data.mds0, type="t")