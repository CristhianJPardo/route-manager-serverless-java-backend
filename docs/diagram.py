from diagrams import Diagram
from diagrams.onprem.vcs import Github
from diagrams.aws.devtools import Codebuild, Codepipeline
from diagrams.aws.management import Cloudformation
from diagrams.aws.storage import S3
from diagrams.aws.compute import Lambda
from diagrams.aws.network import APIGateway

with Diagram("CI/CD Java Lambda", filename="docs/architecture", outformat="png", direction="LR"):
    repo    = Github("GitHub Repo")
    build   = Codebuild("Maven Build & Shade")
    verify  = Codebuild("Verify JAR\ncontains handler")
    package = Codepipeline("SAM Package & Deploy")
    cf      = Cloudformation("CloudFormation\n(Stack)")
    bucket  = S3("S3 Bucket\n(Artefactos)")
    fn      = Lambda("HelloWorldFunction")
    api     = APIGateway("API Gateway\n/hello")

    repo >> build >> verify >> package >> cf
    cf   >> bucket
    cf   >> fn >> api
